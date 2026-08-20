package com.hdc.hdc.adesao.classificacao;

import com.hdc.hdc.adesao.classificacao.dto.DadosCalculoAdesaoDTO;
import com.hdc.hdc.adesao.classificacao.repositories.ConsultaAdesaoPacienteRepository;
import com.hdc.hdc.adesao.classificacao.repositories.ResumoAdesaoPacienteRepository;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassificacaoAdesaoService {

    /*
    * Busca os dados, calcula o percentual, busca a classificação anterior,
    * chama a calculadora e persiste o novo ResumoAdesaoPaciente.
    * */

    private static final int DIAS_ANALISADOS = 14;
    private static final String ZONE = "America/Sao_Paulo";

    private final ConsultaAdesaoPacienteRepository consultaRepository;
    private final ResumoAdesaoPacienteRepository resumoRepository;
    private final PacienteRepository pacienteRepository;
    private final CalculadoraClassificacaoAdesao calculadora;

    @Transactional
    public void recalcular(Integer pacienteId) {

        LocalDate dataFinal = LocalDate.now(ZoneId.of(ZONE)).minusDays(1);
        LocalDate dataInicial = dataFinal.minusDays(DIAS_ANALISADOS - 1L);

        DadosCalculoAdesaoDTO dados =
                consultaRepository.buscarDados(
                        pacienteId,
                        dataInicial,
                        dataFinal
                );

        BigDecimal percentual =
                calcularPercentual(
                        dados.realizado(),
                        dados.esperado()
                );

        Optional<ResumoAdesaoPaciente> resumoExistente =
                resumoRepository.findByPacienteId(pacienteId);

        ClassificacaoAdesao classificacaoAnterior =
                resumoExistente
                        .map(ResumoAdesaoPaciente::getClassificacao)
                        .orElse(null);

        ClassificacaoAdesao novaClassificacao =
                calculadora.classificar(
                        dados.esperado(),
                        percentual,
                        classificacaoAnterior
                );

        ResumoAdesaoPaciente resumo =
                resumoExistente.orElseGet(
                        () -> criarResumo(pacienteId)
                );

        resumo.setPeriodoInicio(dataInicial);
        resumo.setPeriodoFim(dataFinal);
        resumo.setEsperado((int) dados.esperado());
        resumo.setRealizado((int) dados.realizado());
        resumo.setNaoRealizado((int) dados.naoRealizado());
        resumo.setSemRegistro((int) dados.semRegistro());
        resumo.setPercentual(percentual);
        resumo.setClassificacao(novaClassificacao);
        resumo.setCalculadoEm(LocalDateTime.now(ZoneId.of(ZONE)));

        resumoRepository.save(resumo);
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

    private ResumoAdesaoPaciente criarResumo(Integer pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Paciente não encontrado."
                        )
                );

        ResumoAdesaoPaciente resumo =
                new ResumoAdesaoPaciente();

        resumo.setPaciente(paciente);

        return resumo;
    }
}
