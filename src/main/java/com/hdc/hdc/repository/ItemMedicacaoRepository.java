package com.hdc.hdc.repository;

import com.hdc.hdc.model.associacoes.ItemMedicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMedicacaoRepository extends JpaRepository<ItemMedicacao, Long> {
}
