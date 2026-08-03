package com.hdc.hdc.prescricao_medicamentos.paciente;

import com.hdc.hdc.prescricao_medicamentos.paciente.dto.HistoricoPessoalDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.ItemMedicacaoDiaDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.PrescricaoAtivaPacienteDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.RegistroAdesaoRequestDTO;
import com.hdc.hdc.prescricao_medicamentos.paciente.dto.RegistroAdesaoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/prescricoes/medicamentos/paciente")
public class PrescricaoMedicamentoPacienteController {

    private final PrescricaoMedicamentoPacienteService service;

    /**
     * Lista os itens de medicação que o paciente deve tomar hoje.
     * Considera a frequência de cada item para determinar se deve aparecer no dia atual.
     */
    @GetMapping("/ocorrencias-medicamentos")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<ItemMedicacaoDiaDTO> listarMedicacoesDoDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        ItemMedicacaoDiaDTO itens = service.listarMedicacoesDoDia(data);
        return ResponseEntity.ok(itens);
    }

    /**
     * Registra a adesão de um item de medicação (REALIZADO ou NAO_REALIZADO).
     * Valida período ativo, limite diário e impede registro futuro.
     */
    @PostMapping("/adesao")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public ResponseEntity<RegistroAdesaoResponseDTO> registrarAdesao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody RegistroAdesaoRequestDTO request) {

        RegistroAdesaoResponseDTO resposta = service.registrarAdesao(pacienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Altera um registro de adesão já existente (somente no mesmo dia).
     */
    @PutMapping("/adesao/{adesaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<RegistroAdesaoResponseDTO> alterarAdesao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("adesaoId") Long adesaoId,
            @Valid @RequestBody RegistroAdesaoRequestDTO request) {

        RegistroAdesaoResponseDTO resposta = service.alterarAdesao(pacienteId, adesaoId, request);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Lista todas as prescrições ativas do paciente com itens e frequência em linguagem natural
     */
    @GetMapping("/ativas")
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<List<PrescricaoAtivaPacienteDTO>> listarPrescricoesAtivas(
            @PathVariable("id") Integer pacienteId) {

        List<PrescricaoAtivaPacienteDTO> ativas = service.listarPrescricoesAtivas(pacienteId);
        return ResponseEntity.ok(ativas);
    }

    /**
     * Histórico pessoal de adesão do paciente por semana ou mês.
     */
    @GetMapping("/historico")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<HistoricoPessoalDTO> consultarHistorico(
            @PathVariable("id") Integer pacienteId,
            @RequestParam(value = "periodo", defaultValue = "semana") String periodo) {

        HistoricoPessoalDTO historico = service.consultarHistorico(pacienteId, periodo);
        return ResponseEntity.ok(historico);
    }
}
