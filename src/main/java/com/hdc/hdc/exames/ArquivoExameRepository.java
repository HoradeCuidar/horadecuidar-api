package com.hdc.hdc.exames;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ArquivoExameRepository extends JpaRepository<ArquivoExame, Long> {
    Optional<ArquivoExame> findByExameIdAndAtivoTrue(Long exameId);
}
