package com.hdc.hdc.repository;

import com.hdc.hdc.model.TokenRecuperacao;
import com.hdc.hdc.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<TokenRecuperacao, Long> {

    @Modifying
    @Transactional
    @Query("""
        UPDATE TokenRecuperacao t
        SET t.usado = true
        WHERE t.usuario = :user
        AND t.usado = false
    """)
    void invalidateAllByusuario(@Param("user") Usuario usuario);
}
