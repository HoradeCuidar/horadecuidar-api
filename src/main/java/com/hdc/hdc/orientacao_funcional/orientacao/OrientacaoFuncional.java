package com.hdc.hdc.orientacao_funcional.orientacao;

import com.hdc.hdc.orientacao_funcional.tag.TagFuncional;
import com.hdc.hdc.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orientacao_funcional")
public class OrientacaoFuncional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;

    @Column(name = "finalidade", columnDefinition = "text")
    private String finalidade;

    @Column(name = "url_imagem")
    private String urlImagem;

    @ManyToMany
    @JoinTable(
            name = "orientacao_tag",
            joinColumns = @JoinColumn(name = "orientacao_funcional_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_funcional_id")
    )
    private List<TagFuncional> tags = new ArrayList<>();

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(columnDefinition = "text")
    private String resumo;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private com.hdc.hdc.orientacao_funcional.categoria.CategoriaOrientacao categoria;
    private Integer duracaoEstimada;
    @Column(columnDefinition = "text")
    private String materiais;
    @Enumerated(EnumType.STRING)
    private StatusOrientacao status = StatusOrientacao.RASCUNHO;
    @ManyToOne @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;
    private LocalDateTime publicadoEm;
    private boolean cuidadosRevisados;
    @Version private Long versao;
    @OneToMany(mappedBy="orientacao", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("ordem ASC")
    private List<PassoOrientacao> passos = new ArrayList<>();
    @OneToMany(mappedBy="orientacao", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("ordem ASC")
    private List<CuidadoOrientacao> cuidados = new ArrayList<>();
    @OneToMany(mappedBy="orientacao", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("ordem ASC")
    private List<MidiaOrientacao> midias = new ArrayList<>();
}
