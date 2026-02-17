package com.hdc.hdc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRecuperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID token;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    private LocalDateTime expiracao;

    private boolean usado;
}
