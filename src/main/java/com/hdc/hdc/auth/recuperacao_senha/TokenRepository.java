package com.hdc.hdc.auth.recuperacao_senha;

import com.hdc.hdc.usuarios.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    Optional<TokenRecuperacao> findByToken(String token);
}
