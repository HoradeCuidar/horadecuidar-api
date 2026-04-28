package com.hdc.hdc.prescricao_medicamentos.associacoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMedicacaoRepository extends JpaRepository<ItemMedicacao, Long> {
}
