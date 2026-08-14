package com.hdc.hdc.adesao.classificacao.repositories;

import com.hdc.hdc.adesao.classificacao.ClassificacaoAdesao;
import com.hdc.hdc.adesao.classificacao.ResumoAdesaoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumoAdesaoPacienteRepository extends JpaRepository<ResumoAdesaoPaciente, Long> {

    Optional<ResumoAdesaoPaciente> findByPacienteId(Integer pacienteId);

    List<ResumoAdesaoPaciente> findByClassificacao(ClassificacaoAdesao classificacao);
}
