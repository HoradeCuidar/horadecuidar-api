package com.hdc.hdc.prescricao_nutricional.adesao_refeicoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcorrenciaRefeicaoRepository extends JpaRepository<OcorrenciaRefeicao, Integer> {

    List<OcorrenciaRefeicao> findByPrescricaoId(Integer prescricaoId);
}
