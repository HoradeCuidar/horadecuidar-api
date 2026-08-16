package com.hdc.hdc.adesao.classificacao;

import com.hdc.hdc.adesao.classificacao.dto.DadosCalculoAdesaoDTO;
import com.hdc.hdc.adesao.classificacao.repositories.ConsultaAdesaoPacienteRepository;
import com.hdc.hdc.adesao.classificacao.repositories.ResumoAdesaoPacienteRepository;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * menos de 7 ocorrências
 * → DADOS_INSUFICIENTES
 *
 * < 70%
 * → BAIXA_ADESÃO
 *
 * 70% até < 80%
 * se vinda de adequado fica como atenção,
 * se vier de baixa adesão então permanece
 * → ATENÇÃO
 *
 * >= 80%
 * → ADEQUADA
*/

@Component
@Slf4j
@RequiredArgsConstructor
public class CalculadoraClassificacaoAdesao {

    private static final int MINIMO_OCORRENCIAS = 7;
    private static final BigDecimal LIMITE_BAIXA_ADESAO = new BigDecimal("70");
    private static final BigDecimal LIMITE_ADESAO_ADEQUADA = new BigDecimal("80");
    private static final String ZONE = "America/Sao_Paulo"; 

    private final ResumoAdesaoPacienteRepository resumoAdesaoPacienteRepository;
    private final ConsultaAdesaoPacienteRepository consultaAdesaoPacienteRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional
    public void recalcular(Integer pacienteId) {

        LocalDate dataFinal = LocalDate.now(ZoneId.of(ZONE)).minusDays(1);
        LocalDate dataInicial = dataFinal.minusDays(13);

        DadosCalculoAdesaoDTO dados =
                consultaAdesaoPacienteRepository.buscarDados(
                        pacienteId,
                        dataInicial,
                        dataFinal
                );

        BigDecimal percentual = calcularPercentual(dados.realizado(), dados.esperado());

        ResumoAdesaoPaciente resumo = resumoAdesaoPacienteRepository
                .findByPacienteId(pacienteId)
                .orElse(criarResumo(pacienteId));

        // Descobre a classificação anterior
        ClassificacaoAdesao classificacaoAnterior =
                resumo.getClassificacao() == null
                        ? ClassificacaoAdesao.DADOS_INSUFICIENTES
                        : resumo.getClassificacao();

        ClassificacaoAdesao novaClassificacao =
                classificar(
                        dados.esperado(),
                        percentual,
                        classificacaoAnterior
                );

        // Atualizar os dados
        resumo.setPeriodoInicio(dataInicial);
        resumo.setPeriodoFim(dataFinal);

        resumo.setEsperado((int) dados.esperado());
        resumo.setRealizado((int) dados.realizado());
        resumo.setNaoRealizado((int) dados.naoRealizado());
        resumo.setSemRegistro((int) dados.semRegistro());

        resumo.setPercentual(percentual);
        resumo.setClassificacao(novaClassificacao);
        resumo.setCalculadoEm(LocalDateTime.now(ZoneId.of(ZONE)));

        resumoAdesaoPacienteRepository.save(resumo);
    }

    public ClassificacaoAdesao classificar(
            Integer esperado,
            BigDecimal percentual,
            ClassificacaoAdesao classificacaoAnterior
    ) {
        if (esperado < MINIMO_OCORRENCIAS) {
            return ClassificacaoAdesao.DADOS_INSUFICIENTES;
        }

        if (percentual.compareTo(LIMITE_BAIXA_ADESAO) < 0) {
            return ClassificacaoAdesao.BAIXA_ADESAO;
        }

        if (percentual.compareTo(LIMITE_ADESAO_ADEQUADA) >= 0) {
            return ClassificacaoAdesao.ADEQUADA;
        }

        if (classificacaoAnterior == ClassificacaoAdesao.BAIXA_ADESAO) {
            return ClassificacaoAdesao.BAIXA_ADESAO;
        }

        return ClassificacaoAdesao.ATENCAO;
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

    private ResumoAdesaoPaciente criarResumo(
            Integer pacienteId
    ) {
        Paciente paciente = pacienteRepository
                .findById(pacienteId)
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
