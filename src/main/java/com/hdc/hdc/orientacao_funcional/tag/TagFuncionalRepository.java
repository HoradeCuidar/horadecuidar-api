package com.hdc.hdc.orientacao_funcional.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagFuncionalRepository extends JpaRepository<TagFuncional, Long> {
    Optional<TagFuncional> findByNome(String nome);
}
