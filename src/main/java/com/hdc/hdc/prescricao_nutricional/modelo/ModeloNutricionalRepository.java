package com.hdc.hdc.prescricao_nutricional.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModeloNutricionalRepository extends JpaRepository<ModeloNutricional, Integer> {
    List<ModeloNutricional> findAllByOrderByNomeAsc();
}
