package com.hdc.hdc.adesao_medicamentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdesaoMedicamentoRepository extends JpaRepository<AdesaoMedicamento, Long> {
    List<AdesaoMedicamento> findByPrescricaoId(UUID prescricaoId);

    boolean existsByPrescricaoId(UUID prescricaoId);
}
