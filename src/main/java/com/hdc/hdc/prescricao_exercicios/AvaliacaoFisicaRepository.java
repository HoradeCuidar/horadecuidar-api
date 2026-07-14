package com.hdc.hdc.prescricao_exercicios;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, UUID> {
}
