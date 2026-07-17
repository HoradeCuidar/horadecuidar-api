package com.hdc.hdc.orientacao_funcional.orientacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrientacaoFuncionalRepository extends JpaRepository<OrientacaoFuncional, Long> {

    Page<OrientacaoFuncional> findAllByAtivoTrue(Pageable pageable);
}

