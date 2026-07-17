package com.hdc.hdc.avaliacao_fisica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, Long> {
    Page<AvaliacaoFisica> findByPacienteIdOrderByDataRegistroDesc(Integer pacienteId, Pageable pageable);
    Optional<AvaliacaoFisica> findFirstByPacienteIdOrderByDataRegistroDesc(Integer pacienteId);
}

