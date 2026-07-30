package com.hdc.hdc.relatorio.adesao;

import com.hdc.hdc.relatorio.adesao.dto.DadosResumoAdesaoMedicamento;
import com.hdc.hdc.relatorio.adesao.dto.ResumoMedicamentoDTO;
import com.hdc.hdc.util.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RelatorioAdesaoMedicamentoService {

    private final RelatorioAdesaoMedicamentoRepository repository;

    @Transactional(readOnly = true)
    public ResumoMedicamentoDTO obterResumo(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        validarPeriodo(dataInicial, dataFinal);

        LocalDate ontem = LocalDate.now().minusDays(1);
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
}
