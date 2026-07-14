package com.hdc.hdc.orientacao_exercicio.dto;

import com.hdc.hdc.orientacao_exercicio.enums.CategoriaExercicio;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExercicioOrientacaoRequestDTO {
    @NotBlank(message = "O nome do exercício é obrigatório")
    private String nome;
    private String finalidade;
    
    @NotNull(message = "A categoria do exercício é obrigatória")
    private CategoriaExercicio categoria;
    
    private String descricao;
    private MultipartFile imagem;
}
