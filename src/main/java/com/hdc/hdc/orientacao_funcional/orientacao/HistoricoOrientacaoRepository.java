package com.hdc.hdc.orientacao_funcional.orientacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface HistoricoOrientacaoRepository extends JpaRepository<HistoricoOrientacao, Long> {
 List<HistoricoOrientacao> findByOrientacaoIdOrderByDataAlteracaoDesc(Long id);
}
