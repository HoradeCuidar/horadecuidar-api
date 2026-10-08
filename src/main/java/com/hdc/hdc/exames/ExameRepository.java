package com.hdc.hdc.exames;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExameRepository extends JpaRepository<Exame, Long> {
    List<Exame> findByPacienteIdOrderByDataColetaDesc(Integer pacienteId);
    List<Exame> findByStatusAndDisponibilizacaoEmLessThanEqual(StatusExame status, Instant instante);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Exame> findWithLockById(Long id);
}
