package com.hdc.hdc.repository;

import com.hdc.hdc.model.RegistroAdesaoMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistroAdesaoMedicamentoRepository extends JpaRepository<RegistroAdesaoMedicamento, Long> {
    List<RegistroAdesaoMedicamento> findByPrescricaoId(UUID prescricaoId);

    boolean existsByPrescricaoId(UUID prescricaoId);
}
