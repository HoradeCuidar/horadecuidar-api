package com.hdc.hdc.relatorio.adesao;

import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.relatorio.adesao.dto.*;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioAdesaoMedicamentoService {

    private final RelatorioAdesaoMedicamentoRepository repository;
    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public ResumoMedicamentoDTO obterResumo(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        validarPeriodo(dataInicial, dataFinal);

        LocalDate ontem = LocalDate.now(ZoneId.systemDefault()).minusDays(1);
        LocalDate dataFinalConsiderada =
                dataFinal.isAfter(ontem) ? ontem : dataFinal;

        if (dataInicial.isAfter(dataFinalConsiderada)) {
            return resumoSemDados(
                    dataInicial,
                    dataFinalConsiderada
            );
        }

        DadosResumoAdesaoMedicamento dados =
                repository.buscarResumo(
                        pacienteId,
                        dataInicial,
                        dataFinalConsiderada
                );

        BigDecimal percentual = calcularPercentual(
                dados.realizado(),
                dados.esperado()
        );

        return new ResumoMedicamentoDTO(
                dataInicial,
                dataFinalConsiderada,
                dados.esperado(),
                dados.realizado(),
                dados.naoRealizado(),
                dados.semRegistro(),
                percentual
        );
    }

    @Transactional(readOnly = true)
    public EvolucaoAdesaoMedicamentoDTO obterEvolucaoSemanal(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        validarPeriodo(dataInicial, dataFinal);

        LocalDate ultimaDataConcluida =
                LocalDate.now(ZoneId.systemDefault()).minusDays(1);

        LocalDate dataFinalConsiderada =
                dataFinal.isAfter(ultimaDataConcluida)
                        ? ultimaDataConcluida
                        : dataFinal;

        if (dataInicial.isAfter(dataFinalConsiderada)) {
            return new EvolucaoAdesaoMedicamentoDTO(
                    dataInicial,
                    dataFinalConsiderada,
                    "SEMANAL",
                    List.of()
            );
        }

        List<DadosEvolucaoSemanalMedicamento> dados =
                repository.buscarEvolucaoSemanal(
                        pacienteId,
                        dataInicial,
                        dataFinalConsiderada
                );

        List<PeriodoEvolucaoMedicamentoDTO> periodos =
                dados.stream()
                        .map(dado -> montarPeriodo(
                                dado,
                                dataInicial,
                                dataFinalConsiderada
                        ))
                        .toList();

        // Preenchimento de semanas ausentes se necessário futuramente.

        return new EvolucaoAdesaoMedicamentoDTO(
                dataInicial,
                dataFinalConsiderada,
                "SEMANAL",
                periodos
        );
    }

    @Transactional(readOnly = true)
    public DetalhamentoAdesaoMedicamentoResponseDTO obterDetalhamento(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal,
            int pagina,
            int tamanho
    ) {
        validarPaciente(pacienteId);
        validarPeriodo(dataInicial, dataFinal);

        LocalDate ultimaDataConcluida = LocalDate.now(ZoneId.systemDefault()).minusDays(1);

        LocalDate dataFinalConsiderada =
                dataFinal.isAfter(ultimaDataConcluida)
                        ? ultimaDataConcluida
                        : dataFinal;

        if (dataInicial.isAfter(dataFinalConsiderada)) {
            return new DetalhamentoAdesaoMedicamentoResponseDTO(
                    dataInicial,
                    dataFinalConsiderada,
                    toPageDetalhamentoMedicacao(new ArrayList<>(), tamanho)
            );
        }

        List<DadosDetalhamentoDiarioMedicamento> dados =
                repository.buscarDetalhamentoDiario(
                        pacienteId,
                        dataInicial,
                        dataFinalConsiderada,
                        pagina,
                        tamanho
                );

        return new DetalhamentoAdesaoMedicamentoResponseDTO(
                dataInicial,
                dataFinalConsiderada,
                toPageDetalhamentoMedicacao(dados, tamanho));
    }

    /*
    * Métodos auxiliares de verificação ou construção
    * */
    private PeriodoEvolucaoMedicamentoDTO montarPeriodo(
            DadosEvolucaoSemanalMedicamento dado,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        LocalDate inicioSemana = dado.inicioSemana();
        LocalDate fimSemana = inicioSemana.plusDays(6);

        LocalDate inicioConsiderado =
                inicioSemana.isBefore(dataInicial)
                        ? dataInicial
                        : inicioSemana;

        LocalDate fimConsiderado =
                fimSemana.isAfter(dataFinal)
                        ? dataFinal
                        : fimSemana;

        BigDecimal percentual = calcularPercentual(
                dado.realizado(),
                dado.esperado()
        );

        return new PeriodoEvolucaoMedicamentoDTO(
                inicioSemana,
                fimSemana,
                inicioConsiderado,
                fimConsiderado,
                dado.esperado(),
                dado.realizado(),
                dado.naoRealizado(),
                dado.semRegistro(),
                percentual
        );
    }

    private BigDecimal calcularPercentual(
            long realizado,
            long esperado
    ) {
        if (esperado == 0) {
            return null;
        }

        return BigDecimal.valueOf(realizado)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(esperado),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private void validarPeriodo(
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        if (dataInicial == null || dataFinal == null) {
            throw new InvalidValueException(
                    "periodo",
                    "As datas inicial e final são obrigatórias."
            );
        }

        if (dataInicial.isAfter(dataFinal)) {
            throw new InvalidValueException(
                    "periodo",
                    "A data inicial não pode ser posterior à data final."
            );
        }
    }

    private ResumoMedicamentoDTO resumoSemDados(
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        return new ResumoMedicamentoDTO(
                dataInicial,
                dataFinal,
                0,
                0,
                0,
                0,
                null
        );
    }

    private DetalhamentoDiarioMedicamentoDTO toDetalhamentoDTO(
            DadosDetalhamentoDiarioMedicamento dados
    ) {
        return new DetalhamentoDiarioMedicamentoDTO(
                dados.data(),
                "MEDICAMENTO",
                dados.esperado(),
                dados.realizado(),
                dados.naoRealizado(),
                dados.semRegistro(),
                calcularPercentual(
                        dados.realizado(),
                        dados.esperado()
                )
        );
    }

    private void validarPaciente(Integer pacienteId) {
        if (pacienteRepository.findById(pacienteId).isEmpty()) {
            throw new ResourceNotFoundException("pacienteId", "Paciente não encontrado.");
        }
    }

    private Page<DetalhamentoDiarioMedicamentoDTO> toPageDetalhamentoMedicacao(List<DadosDetalhamentoDiarioMedicamento> dados, Integer tamanho) {
        long totalElementos = dados.isEmpty()
                ? 0
                : dados.getFirst().totalElementos();

        int totalPaginas = totalElementos == 0
                ? 0
                : (int) Math.ceil(
                (double) totalElementos / tamanho
        );

        Pageable pageable = PageRequest.of(totalPaginas, tamanho);

        List<DetalhamentoDiarioMedicamentoDTO> conteudo =
                dados.stream()
                        .map(this::toDetalhamentoDTO)
                        .toList();

        return new PageImpl<>(conteudo, pageable, dados.size());
    }
}
