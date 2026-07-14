package com.hdc.hdc.orientacao_exercicio;

import com.hdc.hdc.orientacao_exercicio.enums.CategoriaExercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ExercicioOrientacaoRepository extends JpaRepository<ExercicioOrientacao, UUID> {
    List<ExercicioOrientacao> findByCategoria(CategoriaExercicio categoria);
}
