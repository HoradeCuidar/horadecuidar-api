package com.hdc.hdc.prescricao_exercicios.dto;

import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicioDTO;
import com.hdc.hdc.usuarios.dto.UsuarioDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class PrescricaoExercicioResponseDTO {
    private UUID id;
    private UsuarioDTO paciente;
    private UsuarioDTO profissional;
    private String nomeProfissional;
    private Date dataInicio;
    private Date dataFim;
    private String observacao;
    private boolean ativo;
    private List<ItemExercicioDTO> exercicios;
}
