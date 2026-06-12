package com.hdc.hdc.prescricao_exercicios.paciente;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.prescricao_exercicios.PrescricaoExercicio;
import com.hdc.hdc.prescricao_exercicios.PrescricaoExercicioRepository;
import com.hdc.hdc.prescricao_exercicios.RealizacaoExercicio;
import com.hdc.hdc.prescricao_exercicios.RealizacaoExercicioRepository;
import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicio;
import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicioRepository;
import com.hdc.hdc.prescricao_exercicios.paciente.dto.ExercicioDiaDTO;
import com.hdc.hdc.prescricao_exercicios.paciente.dto.RegistroRealizacaoRequestDTO;
import com.hdc.hdc.prescricao_exercicios.paciente.dto.RegistroRealizacaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrescricaoExercicioPacienteService {

    private final PrescricaoExercicioRepository prescricaoRepository;
    private final RealizacaoExercicioRepository realizacaoRepository;
    private final ItemExercicioRepository itemExercicioRepository;
    private final PacienteRepository pacienteRepository;

    // Listar exercícios do dia
    public List<ExercicioDiaDTO> listarExerciciosDoDia(Integer pacienteId) {
        Date hoje = hoje();
        LocalDate hojeLocal = LocalDate.now();

        List<PrescricaoExercicio> ativas = prescricaoRepository.findAtivasByPacienteId(pacienteId, hoje);
        List<ExercicioDiaDTO> resultado = new ArrayList<>();

        for (PrescricaoExercicio prescricao : ativas) {
            for (ItemExercicio item : prescricao.getExercicios()) {
                if (deveRealizarHoje(item, hojeLocal)) {
                    ExercicioDiaDTO dto = toExercicioDiaDTO(item, prescricao, pacienteId, hoje);
                    resultado.add(dto);
                }
            }
        }

        return resultado;
    }

    // Registrar adesão de exercício pelo paciente
    @Transactional
    public RegistroRealizacaoResponseDTO registrarRealizacao(Integer pacienteId, RegistroRealizacaoRequestDTO request) {
        Paciente paciente = getPaciente(pacienteId);
        ItemExercicio item = getItem(request.getItemExercicioId());
        Date hoje = hoje();

        validarPrescricaoAtiva(item.getPrescricao(), hoje);

        Optional<RealizacaoExercicio> existente = realizacaoRepository
                .findByItemAndPacienteAndDia(item.getId(), pacienteId, hoje);

        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe um registro para este exercício hoje. Use a operação de alteração.");
        }

        RealizacaoExercicio realizacao = new RealizacaoExercicio();
        realizacao.setItemExercicio(item);
        realizacao.setPaciente(paciente);
        realizacao.setStatus(request.getStatus());
        realizacao.setDuracaoRealizadaMinutos(request.getDuracaoRealizadaMinutos());
        realizacao.setObservacao(request.getObservacao());
        realizacao.setDataRegistro(hoje);

        RealizacaoExercicio salva = realizacaoRepository.save(realizacao);
        return toResponseDTO(salva);
    }

    // Altera um registro de realização existente.
    // O paciente só pode alterar registros feitos no dia corrente.
    @Transactional
    public RegistroRealizacaoResponseDTO alterarRealizacao(Integer pacienteId, Long realizacaoId, RegistroRealizacaoRequestDTO request) {
        RealizacaoExercicio realizacao = realizacaoRepository.findById(realizacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de realização não encontrado."));

        if (!realizacao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("O registro não pertence a este paciente.");
        }

        LocalDate dataRegistro = realizacao.getDataRegistro().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        if (!dataRegistro.equals(LocalDate.now())) {
            throw new IllegalStateException("Registros só podem ser alterados no mesmo dia em que foram criados.");
        }

        realizacao.setStatus(request.getStatus());
        realizacao.setDuracaoRealizadaMinutos(request.getDuracaoRealizadaMinutos());
        realizacao.setObservacao(request.getObservacao());

        RealizacaoExercicio atualizada = realizacaoRepository.save(realizacao);
        return toResponseDTO(atualizada);
    }

    /**
     * Determina se um item de exercício deve ser realizado no dia informado
     * com base na frequência prescrita.
     * A distribuição é puramente posicional e não leva em conta
     * preferências do paciente. Refinamentos futuros poderão permitir que o profissional
     * defina os dias específicos.
     */
    private boolean deveRealizarHoje(ItemExercicio item, LocalDate hoje) {
        return switch (item.getFrequenciaTipo()) {
            case DIA -> true;
            case SEMANA -> {
                int diaDaSemana = hoje.getDayOfWeek().getValue(); // 1=seg … 7=dom
                yield diaDaSemana <= item.getFrequenciaValor();
            }
            case MES -> {
                int diaDoMes = hoje.getDayOfMonth();
                int diasNoMes = hoje.lengthOfMonth();
                // distribui sessões uniformemente pelo mês
                int intervalo = diasNoMes / item.getFrequenciaValor();
                yield intervalo > 0 && (diaDoMes % intervalo == 1 || item.getFrequenciaValor() >= diasNoMes);
            }
        };
    }

    // ----------------------
    // Conversores e helpers:

    private ExercicioDiaDTO toExercicioDiaDTO(ItemExercicio item, PrescricaoExercicio prescricao,
                                              Integer pacienteId, Date hoje) {
        ExercicioDiaDTO dto = new ExercicioDiaDTO();
        dto.setItemExercicioId(item.getId());
        dto.setPrescricaoId(prescricao.getId());
        dto.setNomeExercicio(item.getNomeExercicio());
        dto.setTipoExercicio(item.getTipoExercicio());
        dto.setFrequenciaValor(item.getFrequenciaValor());
        dto.setFrequenciaTipo(item.getFrequenciaTipo());
        dto.setDuracaoValor(item.getDuracaoValor());
        dto.setUnidadeDuracao(item.getUnidadeDuracao());
        dto.setSeries(item.getSeries());
        dto.setRepeticoes(item.getRepeticoes());
        dto.setIntensidade(item.getIntensidade());
        dto.setObservacao(item.getObservacao());

        realizacaoRepository.findByItemAndPacienteAndDia(item.getId(), pacienteId, hoje)
                .ifPresent(r -> {
                    dto.setStatusHoje(r.getStatus());
                    dto.setRealizacaoId(r.getId());
                });

        return dto;
    }

    private RegistroRealizacaoResponseDTO toResponseDTO(RealizacaoExercicio r) {
        RegistroRealizacaoResponseDTO dto = new RegistroRealizacaoResponseDTO();
        dto.setId(r.getId());
        dto.setItemExercicioId(r.getItemExercicio().getId());
        dto.setNomeExercicio(r.getItemExercicio().getNomeExercicio());
        dto.setStatus(r.getStatus());
        dto.setDuracaoRealizadaMinutos(r.getDuracaoRealizadaMinutos());
        dto.setObservacao(r.getObservacao());
        dto.setDataRegistro(r.getDataRegistro());
        return dto;
    }

    private void validarPrescricaoAtiva(PrescricaoExercicio prescricao, Date hoje) {
        if (!prescricao.isAtivo()) {
            throw new IllegalStateException("A prescrição associada a este exercício não está ativa.");
        }
        if (prescricao.getDataFim() != null && prescricao.getDataFim().before(hoje)) {
            throw new IllegalStateException("A prescrição associada a este exercício já foi encerrada.");
        }
    }

    private Paciente getPaciente(Integer id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));
    }

    private ItemExercicio getItem(Long id) {
        return itemExercicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de exercício não encontrado."));
    }

    private Date hoje() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
