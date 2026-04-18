package com.hdc.hdc.model.associacoes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hdc.hdc.model.Medicamento;
import com.hdc.hdc.model.PrescricaoMedicamento;
import com.hdc.hdc.model.enums.DosagemUnidade;
import com.hdc.hdc.model.enums.IntervaloTipo;
import com.hdc.hdc.model.enums.ViaAdministracao;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_medicacao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemMedicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id")
    @JsonIgnore
    private PrescricaoMedicamento prescricao;

    @Column(name = "nome_medicamento")
    private String nomeMedicamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    @Column(name = "dosagem_valor")
    private Double dosagemValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "dosagem_unidade")
    private DosagemUnidade dosagemUnidade;

    @Column(name = "quantidade_doses")
    private Integer quantidadeDoses;

    @Column(name = "intervalo_valor")
    private Integer intervaloValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "intervalo_tipo")
    private IntervaloTipo intervaloTipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "via_administracao")
    private ViaAdministracao viaAdministracao;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

}
