package com.hdc.hdc.orientacao_funcional.orientacao.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record OrientacaoFuncionalRequestDTO(
 @Size(max=100) String nome,
 @Size(max=5000) String descricao,
 @Size(max=5000) String finalidade,
 @Size(max=30) List<Long> tagsIds,
 @Size(max=2000) String resumo,
 Long categoriaId,
 @Min(1) @Max(1440) Integer duracaoEstimada,
 @Size(max=5000) String materiais,
 @Size(max=50) List<@NotNull @Valid PassoDTO> passos,
 @Size(max=100) List<@NotNull @Valid CuidadoDTO> cuidados
) {}
