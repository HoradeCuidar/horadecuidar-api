package com.hdc.hdc.exames;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "arquivos_exame")
@Getter @Setter
public class ArquivoExame {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "exame_id", nullable = false)
    private Long exameId;
    @Column(name = "nome_original", nullable = false)
    private String nomeOriginal;
    @Column(name = "chave_armazenamento", nullable = false)
    private String chaveArmazenamento;
    @Column(name = "mime_type", nullable = false)
    private String mimeType;
    @Column(name = "tamanho_bytes", nullable = false)
    private Long tamanhoBytes;
    @Column(nullable = false)
    private String checksum;
    @Column(name = "enviado_em", nullable = false)
    private Instant enviadoEm;
    @Column(nullable = false)
    private boolean ativo;
}
