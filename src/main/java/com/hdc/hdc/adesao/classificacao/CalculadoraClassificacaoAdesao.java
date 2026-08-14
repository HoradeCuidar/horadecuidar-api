package com.hdc.hdc.adesao.classificacao;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
public class CalculadoraClassificacaoAdesao {

    private static final int MINIMO_OCORRENCIAS = 7;
    private static final BigDecimal LIMITE_BAIXA_ADESAO = new BigDecimal("70");
    private static final BigDecimal LIMITE_ADESAO_ADEQUADA = new BigDecimal("80");

    public ClassificacaoAdesao classificar(
            long esperado,
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
