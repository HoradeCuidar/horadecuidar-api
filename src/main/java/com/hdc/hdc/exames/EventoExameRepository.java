package com.hdc.hdc.exames;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EventoExameRepository extends JpaRepository<EventoExame, Long> {
    List<EventoExame> findByExameIdOrderByOcorridoEmAsc(Long exameId);
}
