package com.hdc.hdc.prescricao_nutricional.adesao_refeicoes;

import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricional;
import com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OcorrenciaRefeicaoService {

    private final OcorrenciaRefeicaoRepository ocorrenciaRefeicaoRepository;

    public void gerarOcorrencias(PrescricaoNutricional prescricao) {
        Set<ChaveOcorrencia> existentes = new HashSet<>();
        for (OcorrenciaRefeicao ocorrencia : ocorrenciaRefeicaoRepository.findByPrescricaoId(prescricao.getId())) {
            existentes.add(new ChaveOcorrencia(ocorrencia.getRefeicao().getId(), ocorrencia.getDataPrevista()));
        }

        List<OcorrenciaRefeicao> novasOcorrencias = new ArrayList<>();

        for (Refeicao refeicao : prescricao.getRefeicoes()) {
            for (LocalDate data = prescricao.getDataInicio();
                 !data.isAfter(prescricao.getDataFim());
                 data = data.plusDays(1)) {

                ChaveOcorrencia chave = new ChaveOcorrencia(refeicao.getId(), data);
                if (existentes.contains(chave)) {
                    continue;
                }

                OcorrenciaRefeicao ocorrencia = new OcorrenciaRefeicao();
                ocorrencia.setPrescricao(prescricao);
                ocorrencia.setRefeicao(refeicao);
                ocorrencia.setOpcao(null);
                ocorrencia.setDataPrevista(data);
                ocorrencia.setOrdemNoDia(refeicao.getOrdem());
                ocorrencia.setStatus(StatusAdesao.PENDENTE);
                ocorrencia.setDataHoraRegistro(null);
                novasOcorrencias.add(ocorrencia);
                existentes.add(chave);
            }
        }

        if (!novasOcorrencias.isEmpty()) {
            ocorrenciaRefeicaoRepository.saveAll(novasOcorrencias);
        }
    }

    @Transactional
    public int finalizarOcorrenciasPendentesExpiradas(LocalDate hoje) {
        return ocorrenciaRefeicaoRepository.marcarPendentesExpiradas(
                StatusAdesao.PENDENTE,
                StatusAdesao.NAO_REALIZADO,
                hoje
        );
    }

    private record ChaveOcorrencia(Integer refeicaoId, LocalDate dataPrevista) {
    }
}
