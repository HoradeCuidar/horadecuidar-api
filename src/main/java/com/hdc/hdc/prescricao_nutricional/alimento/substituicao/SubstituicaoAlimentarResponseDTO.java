package com.hdc.hdc.prescricao_nutricional.alimento.substituicao;
import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Getter @Setter
public class SubstituicaoAlimentarResponseDTO {
    private Integer id;
    @NotBlank private String descricao;
    @NotNull @DecimalMin("0.01") private BigDecimal quantidade;
    @NotNull private UnidadeDeMedida unidade;
    private String medidaCaseira;
    private String observacao;
    @NotNull @Min(1) private Integer ordem;
}
