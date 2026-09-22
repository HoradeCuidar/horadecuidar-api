package com.hdc.hdc.prescricao_nutricional.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoNutricionalUpdateDTO {

    @NotNull(message = "Data final é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    private String observacoes;

    @Valid
    @NotEmpty(message = "A prescrição deve possuir pelo menos uma refeição")
    private List<RefeicaoUpdateDTO> refeicoes;
}