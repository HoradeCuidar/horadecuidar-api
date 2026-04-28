package com.hdc.hdc.prescricao_medicamentos.paciente;

import com.hdc.hdc.adesao_medicamentos.AdesaoMedicamento;
import com.hdc.hdc.adesao_medicamentos.AdesaoMedicamentoRepository;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamentoRepository;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoRepository;
import com.hdc.hdc.prescricao_medicamentos.enums.IntervaloTipo;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.HistoricoPessoalDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.ItemMedicacaoDiaDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.PrescricaoAtivaPacienteDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.RegistroAdesaoRequestDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.RegistroAdesaoResponseDTO;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrescricaoMedicamentoPacienteService {

    private final PrescricaoMedicamentoRepository prescricaoRepository;
    private final AdesaoMedicamentoRepository adesaoRepository;
    private final PacienteRepository pacienteRepository;
    private final ItemMedicacaoRepository itemMedicacaoRepository;

    public List<ItemMedicacaoDiaDTO> listarMedicacoesDoDia(Integer pacienteId) {
        validarPaciente(pacienteId);
        LocalDate hoje = LocalDate.now();
        Date dataRef = Date.from(hoje.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<PrescricaoMedicamento> prescricoesAtivas =
                prescricaoRepository.findAtivasByPacienteId(pacienteId, dataRef);

        List<ItemMedicacaoDiaDTO> itensDoDia = new ArrayList<>();

        for (PrescricaoMedicamento prescricao : prescricoesAtivas) {
            if (prescricao.getMedicacoes() == null) continue;

            for (ItemMedicacao item : prescricao.getMedicacoes()) {
                if (!deveAparecerHoje(item, prescricao, hoje)) continue;

                int dosesEsperadas = calcularDosesEsperadasHoje(item);
                List<AdesaoMedicamento> registrosHoje = buscarRegistrosDoDia(item.getId(), hoje);

                StatusAdesao statusAtual = null;
                Long adesaoId = null;
                if (!registrosHoje.isEmpty()) {
                    AdesaoMedicamento ultimoRegistro = registrosHoje.get(0);
                    statusAtual = ultimoRegistro.getStatus();
                    adesaoId = ultimoRegistro.getId();
                }

                itensDoDia.add(ItemMedicacaoDiaDTO.builder()
                        .itemId(item.getId())
                        .prescricaoId(prescricao.getId())
                        .nomeMedicamento(item.getNomeMedicamento())
                        .dosagemFormatada(formatarDosagem(item))
                        .frequencia(formatarFrequencia(item))
                        .viaAdministracao(item.getViaAdministracao() != null
                                ? item.getViaAdministracao().getName() : null)
                        .observacao(item.getObservacao())
                        .statusAdesaoHoje(statusAtual)
                        .adesaoId(adesaoId)
                        .dosesEsperadasHoje(dosesEsperadas)
                        .dosesRegistradasHoje(registrosHoje.size())
                        .build());
            }
        }

        return itensDoDia;
    }

    @Transactional
    public RegistroAdesaoResponseDTO registrarAdesao(Integer pacienteId, RegistroAdesaoRequestDTO request) {
        validarPaciente(pacienteId);

        ItemMedicacao item = buscarItemMedicacao(request.getItemMedicacaoId());
        PrescricaoMedicamento prescricao = item.getPrescricao();

        // Validar que o item pertence ao paciente
        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new InvalidValueException("itemMedicacaoId",
                    "O item de medicação não pertence a este paciente.");
        }

        LocalDate hoje = LocalDate.now();
        LocalDateTime agora = LocalDateTime.now();

        // Validar que a prescrição está ativa e dentro do período
        validarPrescricaoAtiva(prescricao, hoje);

        // Não permitir registro no futuro
        if (agora.toLocalDate().isAfter(hoje)) {
            throw new InvalidValueException("dataHoraRegistro",
                    "Não é possível registrar adesão para uma data futura.");
        }

        // Verificar limite de registros por dia de acordo com a frequência
        int dosesEsperadas = calcularDosesEsperadasHoje(item);
        List<AdesaoMedicamento> registrosHoje = buscarRegistrosDoDia(item.getId(), hoje);

        if (registrosHoje.size() >= dosesEsperadas) {
            throw new InvalidValueException("itemMedicacaoId",
                    "O limite de registros de adesão para este item hoje já foi atingido ("
                            + dosesEsperadas + " dose(s) esperada(s)).");
        }

        // Criar novo registro
        AdesaoMedicamento adesao = new AdesaoMedicamento();
        adesao.setPrescricao(prescricao);
        adesao.setItemMedicacao(item);
        adesao.setDataHoraRegistro(agora);
        adesao.setStatus(request.getStatus());
        adesao.setObservacao(request.getObservacao());

        AdesaoMedicamento salvo = adesaoRepository.save(adesao);

        return toRegistroAdesaoResponseDTO(salvo);
    }

    @Transactional
    public RegistroAdesaoResponseDTO alterarAdesao(
            Integer pacienteId, Long adesaoId, RegistroAdesaoRequestDTO request) {
        validarPaciente(pacienteId);

        AdesaoMedicamento adesao = adesaoRepository.findById(adesaoId)
                .orElseThrow(() -> new ResourceNotFoundException("adesaoId",
                        "Registro de adesão não encontrado."));

        // Validar que pertence ao paciente
        if (!adesao.getPrescricao().getPaciente().getId().equals(pacienteId)) {
            throw new InvalidValueException("adesaoId",
                    "O registro de adesão não pertence a este paciente.");
        }

        // Só pode alterar registros do mesmo dia
        LocalDate hoje = LocalDate.now();
        LocalDate dataRegistro = adesao.getDataHoraRegistro().toLocalDate();
        if (!dataRegistro.equals(hoje)) {
            throw new InvalidValueException("adesaoId",
                    "Não é possível alterar um registro de adesão de dias anteriores.");
        }

        adesao.setStatus(request.getStatus());
        adesao.setObservacao(request.getObservacao());
        adesao.setDataHoraRegistro(LocalDateTime.now());

        AdesaoMedicamento salvo = adesaoRepository.save(adesao);
        return toRegistroAdesaoResponseDTO(salvo);
    }

    public List<PrescricaoAtivaPacienteDTO> listarPrescricoesAtivas(Integer pacienteId) {
        validarPaciente(pacienteId);
        Date dataRef = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<PrescricaoMedicamento> ativas =
                prescricaoRepository.findAtivasByPacienteId(pacienteId, dataRef);

        return ativas.stream()
                .map(this::toPrescricaoAtivaPacienteDTO)
                .toList();
    }

    public HistoricoPessoalDTO consultarHistorico(Integer pacienteId, String periodo) {
        validarPaciente(pacienteId);

        LocalDate hoje = LocalDate.now();
        LocalDate inicio;
        LocalDate fim;
        String periodoLabel;

        if ("mes".equalsIgnoreCase(periodo)) {
            inicio = hoje.withDayOfMonth(1);
            fim = hoje.withDayOfMonth(hoje.lengthOfMonth()).plusDays(1);
            periodoLabel = "Mês atual (" + inicio + " a " + fim.minusDays(1) + ")";
        } else {
            // Padrão: semana
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            inicio = hoje.with(weekFields.dayOfWeek(), 1);
            fim = inicio.plusDays(7);
            periodoLabel = "Semana atual (" + inicio + " a " + fim.minusDays(1) + ")";
        }

        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.atStartOfDay();

        // Buscar todas as adesões do paciente no período
        List<AdesaoMedicamento> adesoes =
                adesaoRepository.findByPacienteIdAndPeriodo(pacienteId, inicioDateTime, fimDateTime);

        // Calcular total de itens esperados no período
        Date dataRef = Date.from(hoje.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<PrescricaoMedicamento> prescricoesAtivas =
                prescricaoRepository.findAtivasByPacienteId(pacienteId, dataRef);

        int totalEsperado = calcularTotalEsperadoNoPeriodo(prescricoesAtivas, inicio, fim);

        int realizados = (int) adesoes.stream()
                .filter(a -> a.getStatus() == StatusAdesao.REALIZADO)
                .count();

        int naoRealizados = (int) adesoes.stream()
                .filter(a -> a.getStatus() == StatusAdesao.NAO_REALIZADO)
                .count();

        double percentual = totalEsperado > 0
                ? ((double) realizados / totalEsperado) * 100
                : 0.0;

        return HistoricoPessoalDTO.builder()
                .periodo(periodoLabel)
                .dataInicio(inicio)
                .dataFim(fim.minusDays(1))
                .totalItensEsperados(totalEsperado)
                .totalItensRealizados(realizados)
                .totalItensNaoRealizados(naoRealizados)
                .percentualAdesao(Math.round(percentual * 100.0) / 100.0)
                .build();
    }

    //  Métodos auxiliares — lógica de frequência

    /**
     * Determina se um item de medicação deve aparecer hoje com base na
     * frequência (quantidadeDoses / intervaloValor / intervaloTipo)
     * e na dataInicio da prescrição.
     */
    private boolean deveAparecerHoje(ItemMedicacao item, PrescricaoMedicamento prescricao, LocalDate hoje) {
        LocalDate dataInicio = prescricao.getDataInicio()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (hoje.isBefore(dataInicio)) return false;

        if (prescricao.getDataFim() != null) {
            LocalDate dataFim = prescricao.getDataFim()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (hoje.isAfter(dataFim)) return false;
        }

        IntervaloTipo tipo = item.getIntervaloTipo();
        int intervaloValor = item.getIntervaloValor() != null ? item.getIntervaloValor() : 1;

        return switch (tipo) {
            case HORA -> true; // Se o intervalo é por hora, aparece todo dia
            case DIA -> {
                long diasDesdeInicio = ChronoUnit.DAYS.between(dataInicio, hoje);
                yield diasDesdeInicio % intervaloValor == 0;
            }
            case SEMANA -> {
                long semanasDesdeInicio = ChronoUnit.WEEKS.between(dataInicio, hoje);
                yield semanasDesdeInicio % intervaloValor == 0
                        && dataInicio.getDayOfWeek() == hoje.getDayOfWeek();
            }
            case MES -> {
                long mesesDesdeInicio = ChronoUnit.MONTHS.between(dataInicio, hoje);
                yield mesesDesdeInicio % intervaloValor == 0
                        && dataInicio.getDayOfMonth() == hoje.getDayOfMonth();
            }
        };
    }

    /**
     * Calcula quantas doses são esperadas hoje para um item.
     * Ex: quantidadeDoses=2, intervaloTipo=DIA → 2 doses por dia.
     */
    private int calcularDosesEsperadasHoje(ItemMedicacao item) {
        Integer doses = item.getQuantidadeDoses();
        return doses != null && doses > 0 ? doses : 1;
    }

    /**
     * Calcula o total de doses esperadas de todas as prescrições ativas
     * no período [inicio, fim).
     */
    private int calcularTotalEsperadoNoPeriodo(
            List<PrescricaoMedicamento> prescricoes, LocalDate inicio, LocalDate fim) {
        int total = 0;
        LocalDate hoje = LocalDate.now();

        for (PrescricaoMedicamento prescricao : prescricoes) {
            if (prescricao.getMedicacoes() == null) continue;
            for (ItemMedicacao item : prescricao.getMedicacoes()) {
                // Contar quantos dias do período o item deveria ter aparecido
                // Limitar até hoje (não contar dias futuros como esperados)
                LocalDate limiteMax = fim.isBefore(hoje) ? fim : hoje.plusDays(1);

                for (LocalDate dia = inicio; dia.isBefore(limiteMax); dia = dia.plusDays(1)) {
                    if (deveAparecerHoje(item, prescricao, dia)) {
                        total += calcularDosesEsperadasHoje(item);
                    }
                }
            }
        }
        return total;
    }

    //  Métodos auxiliares — formatação

    private String formatarDosagem(ItemMedicacao item) {
        if (item.getDosagemValor() == null || item.getDosagemUnidade() == null) return "";

        double valor = item.getDosagemValor();
        String unidade = item.getDosagemUnidade().getValue();

        // Formatar valor: remover .0 se for inteiro
        String valorStr = valor == Math.floor(valor)
                ? String.valueOf((int) valor)
                : String.valueOf(valor);

        // Pluralizar unidade simples
        if (valor > 1 && !unidade.endsWith("s")) {
            unidade = unidade + "s";
        }

        return valorStr + " " + unidade;
    }

    /**
     * Gera uma descrição em linguagem natural da frequência.
     * Ex: "2 vezes ao dia", "1 vez a cada 2 dias", "1 vez por semana"
     */
    private String formatarFrequencia(ItemMedicacao item) {
        Integer doses = item.getQuantidadeDoses();
        Integer intervalo = item.getIntervaloValor();
        IntervaloTipo tipo = item.getIntervaloTipo();

        if (doses == null || tipo == null) return "";

        int qtdDoses = doses;
        int intervaloVal = intervalo != null ? intervalo : 1;

        String unidadeTempo = switch (tipo) {
            case HORA -> intervaloVal == 1 ? "hora" : "horas";
            case DIA -> intervaloVal == 1 ? "dia" : "dias";
            case SEMANA -> intervaloVal == 1 ? "semana" : "semanas";
            case MES -> intervaloVal == 1 ? "mês" : "meses";
        };

        String vezStr = qtdDoses == 1 ? "vez" : "vezes";

        if (tipo == IntervaloTipo.HORA) {
            // Ex: "1 vez a cada 8 horas"
            return qtdDoses + " " + vezStr + " a cada " + intervaloVal + " " + unidadeTempo;
        }

        if (intervaloVal == 1) {
            // Ex: "2 vezes ao dia", "1 vez por semana"
            String preposicao = tipo == IntervaloTipo.DIA ? "ao" : "por";
            return qtdDoses + " " + vezStr + " " + preposicao + " " + unidadeTempo;
        }

        // Ex: "1 vez a cada 2 dias"
        return qtdDoses + " " + vezStr + " a cada " + intervaloVal + " " + unidadeTempo;
    }

    //  Métodos auxiliares — validação e busca

    private void validarPaciente(Integer pacienteId) {
        pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("pacienteId",
                        "Paciente não encontrado."));
    }

    private ItemMedicacao buscarItemMedicacao(Long itemId) {
        return itemMedicacaoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("itemMedicacaoId",
                        "Item de medicação não encontrado."));
    }

    private void validarPrescricaoAtiva(PrescricaoMedicamento prescricao, LocalDate hoje) {
        if (!prescricao.isAtivo()) {
            throw new InvalidValueException("prescricao",
                    "A prescrição não está ativa.");
        }

        LocalDate dataInicio = prescricao.getDataInicio()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (hoje.isBefore(dataInicio)) {
            throw new InvalidValueException("prescricao",
                    "A prescrição ainda não iniciou.");
        }

        if (prescricao.getDataFim() != null) {
            LocalDate dataFim = prescricao.getDataFim()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (hoje.isAfter(dataFim)) {
                throw new InvalidValueException("prescricao",
                        "O período da prescrição já encerrou.");
            }
        }
    }

    private List<AdesaoMedicamento> buscarRegistrosDoDia(Long itemMedicacaoId, LocalDate dia) {
        LocalDateTime inicioDia = dia.atStartOfDay();
        LocalDateTime fimDia = dia.plusDays(1).atStartOfDay();
        return adesaoRepository.findByItemMedicacaoIdAndDia(itemMedicacaoId, inicioDia, fimDia);
    }

    //  Métodos auxiliares — mapeamento DTO

    private PrescricaoAtivaPacienteDTO toPrescricaoAtivaPacienteDTO(PrescricaoMedicamento prescricao) {
        List<PrescricaoAtivaPacienteDTO.ItemResumidoDTO> itens = List.of();
        if (prescricao.getMedicacoes() != null) {
            itens = prescricao.getMedicacoes().stream()
                    .map(item -> PrescricaoAtivaPacienteDTO.ItemResumidoDTO.builder()
                            .itemId(item.getId())
                            .nomeMedicamento(item.getNomeMedicamento())
                            .dosagemFormatada(formatarDosagem(item))
                            .frequencia(formatarFrequencia(item))
                            .viaAdministracao(item.getViaAdministracao() != null
                                    ? item.getViaAdministracao().getName() : null)
                            .build())
                    .toList();
        }

        String nomeProfissional = prescricao.getProfissional() != null
                ? prescricao.getProfissional().getNome()
                : null;

        return PrescricaoAtivaPacienteDTO.builder()
                .id(prescricao.getId())
                .nomeProfissional(nomeProfissional)
                .dataInicio(prescricao.getDataInicio())
                .dataFim(prescricao.getDataFim())
                .itens(itens)
                .build();
    }

    private RegistroAdesaoResponseDTO toRegistroAdesaoResponseDTO(AdesaoMedicamento adesao) {
        return RegistroAdesaoResponseDTO.builder()
                .id(adesao.getId())
                .itemMedicacaoId(adesao.getItemMedicacao().getId())
                .nomeMedicamento(adesao.getItemMedicacao().getNomeMedicamento())
                .status(adesao.getStatus())
                .observacao(adesao.getObservacao())
                .dataHoraRegistro(adesao.getDataHoraRegistro())
                .build();
    }
}
