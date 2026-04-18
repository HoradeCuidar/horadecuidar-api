package com.hdc.hdc.repository;

import com.hdc.hdc.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    @Query("""
        SELECT m FROM Medicamento m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
    """)
    List<Medicamento> buscarPorNomeSugestoes(@Param("nome") String nome);

    Optional<Medicamento> findByNomeIgnoreCase(String nome);
}
