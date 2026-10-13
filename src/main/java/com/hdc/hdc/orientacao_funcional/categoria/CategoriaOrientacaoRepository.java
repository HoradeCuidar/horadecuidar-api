package com.hdc.hdc.orientacao_funcional.categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoriaOrientacaoRepository extends JpaRepository<CategoriaOrientacao, Long> {
 List<CategoriaOrientacao> findAllByAtivoTrueOrderByNomeAsc();
}
