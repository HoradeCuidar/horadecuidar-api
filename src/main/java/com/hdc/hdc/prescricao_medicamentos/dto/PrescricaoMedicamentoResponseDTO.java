package com.hdc.hdc.prescricao_medicamentos.dto;

import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;
import com.hdc.hdc.usuarios.dto.UsuarioDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class PrescricaoMedicamentoResponseDTO {
    private UUID id;
    private UsuarioDTO pacienteId;
    private UsuarioDTO profissionalId;
    private String nomeProfissional;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String observacao;
    private boolean ativo;
    private List<ItemMedicacaoDTO> medicacoes;
}
