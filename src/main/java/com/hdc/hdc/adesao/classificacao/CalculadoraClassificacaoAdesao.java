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

    /*
    * Recebe esperado, percentual e classificacaoAnterior e
    * responde qual é a nova classificação.
    * */

    private static final int MINIMO_OCORRENCIAS = 7;
    private static final BigDecimal LIMITE_BAIXA_ADESAO = new BigDecimal("70");
    private static final BigDecimal LIMITE_ADESAO_ADEQUADA = new BigDecimal("80");
    private static final String ZONE = "America/Sao_Paulo"; 

    private final ResumoAdesaoPacienteRepository resumoAdesaoPacienteRepository;
    private final ConsultaAdesaoPacienteRepository consultaAdesaoPacienteRepository;
    private final PacienteRepository pacienteRepository;

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
}
