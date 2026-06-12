package com.hdc.hdc.prescricao_exercicios.item_exercicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemExercicioRepository extends JpaRepository<ItemExercicio, Long> {
}
