package com.hdc.hdc.orientacao_funcional.orientacao;

import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalRequestDTO;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orientacoes-funcionais")
public class OrientacaoFuncionalController {

    private final OrientacaoFuncionalService orientacaoFuncionalService;

    /**
     * Listagem para pacientes: retorna apenas orientações ativas.
     */
    @GetMapping("/paciente")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<Page<OrientacaoFuncionalResponseDTO>> listarParaPaciente(
            @RequestParam(value = "number-page", defaultValue = "0") Integer numberPage,
            @RequestParam(value = "page-size", defaultValue = "15") Integer pageSize,
            @RequestParam(required=false) Long categoriaId,
            @RequestParam(required=false) Long tagId
    ) {
        return ResponseEntity.ok(orientacaoFuncionalService.listar(true, numberPage, pageSize, categoriaId, tagId));
    }

    /**
     * Listagem para profissionais/admin: retorna todas as orientações, incluindo inativas.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Page<OrientacaoFuncionalResponseDTO>> listarParaProfissional(
            @RequestParam(value = "number-page", defaultValue = "0") Integer numberPage,
            @RequestParam(value = "page-size", defaultValue = "15") Integer pageSize,
            @RequestParam(required=false) Long categoriaId,
            @RequestParam(required=false) Long tagId
    ) {
        return ResponseEntity.ok(orientacaoFuncionalService.listar(false, numberPage, pageSize, categoriaId, tagId));
    }

    /**
     * Busca uma orientação por ID.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<OrientacaoFuncionalResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orientacaoFuncionalService.buscarPorId(id));
    }

    /**
     * Cria uma nova orientação funcional com upload opcional de imagem.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<OrientacaoFuncionalResponseDTO> criar(
            @RequestPart("dados") @Valid OrientacaoFuncionalRequestDTO requestDTO,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @AuthenticationPrincipal Usuario usuario
    ) throws IOException {
        OrientacaoFuncionalResponseDTO response = orientacaoFuncionalService.criarOrientacao(requestDTO, imagem, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza os dados de uma orientação existente.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<OrientacaoFuncionalResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestPart("dados") @Valid OrientacaoFuncionalRequestDTO requestDTO,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem
    ) throws IOException {
        return ResponseEntity.ok(orientacaoFuncionalService.atualizarOrientacao(id, requestDTO, imagem));
    }

    /**
     * Ativa ou inativa uma orientação funcional.
     * Corpo: { "ativo": true } ou { "ativo": false }
     */
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<OrientacaoFuncionalResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativo
    ) {
        return ResponseEntity.ok(orientacaoFuncionalService.alterarStatus(id, ativo));
    }

    /*
     * Deletar prescrição se não houver registros relacionados
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<OrientacaoFuncionalResponseDTO> deletarOrientacao(
            @PathVariable Long id
    ) {
        orientacaoFuncionalService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record RevisaoRequest(boolean cuidadosRevisados) {}

    @PostMapping("/{id}/publicar")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public OrientacaoFuncionalResponseDTO publicar(@PathVariable Long id, @RequestBody RevisaoRequest revisao) {
        return orientacaoFuncionalService.publicar(id, revisao.cuidadosRevisados());
    }

    @PostMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public OrientacaoFuncionalResponseDTO inativar(@PathVariable Long id) {
        return orientacaoFuncionalService.alterarStatus(id, false);
    }

    @GetMapping("/{id}/historico")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public java.util.List<HistoricoOrientacao> historico(@PathVariable Long id) {
        return orientacaoFuncionalService.historico(id);
    }

    @PostMapping(value="/{id}/midias", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public OrientacaoFuncionalResponseDTO adicionarMidia(@PathVariable Long id,
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestPart("dados") @Valid com.hdc.hdc.orientacao_funcional.orientacao.dto.MidiaRequestDTO dados) throws IOException {
        return orientacaoFuncionalService.adicionarMidia(id, arquivo, dados);
    }

    @PutMapping("/{id}/midias/{midiaId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public OrientacaoFuncionalResponseDTO atualizarMidia(@PathVariable Long id, @PathVariable Long midiaId,
            @RequestBody @Valid com.hdc.hdc.orientacao_funcional.orientacao.dto.MidiaRequestDTO dados) {
        return orientacaoFuncionalService.atualizarMidia(id, midiaId, dados);
    }

    @DeleteMapping("/{id}/midias/{midiaId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public OrientacaoFuncionalResponseDTO removerMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        return orientacaoFuncionalService.removerMidia(id, midiaId);
    }
}
