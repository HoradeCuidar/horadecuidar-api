package com.hdc.hdc.exames;

import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExameController {
    private final ExameService service;
    public ExameController(ExameService service) { this.service = service; }

    @PostMapping(value = "/participantes/{id}/exames", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ExameDTO> cadastrar(
        @PathVariable Integer id,
        @Valid @RequestPart("dados") DadosExame dados,
        @RequestPart("arquivo") MultipartFile arquivo,
        @CurrentUser Usuario autor
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(id, dados, arquivo, autor));
    }

    @GetMapping("/participantes/{id}/exames")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
    public List<ExameDTO> listar(@PathVariable Integer id, @CurrentUser Usuario autor) {
        return service.listar(id, autor);
    }

    @GetMapping("/exames/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
    public ExameDTO consultar(@PathVariable Long id, @CurrentUser Usuario autor) {
        return service.consultar(id, autor);
    }

    @GetMapping("/exames/{id}/arquivo")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
    public ResponseEntity<byte[]> baixar(@PathVariable Long id, @CurrentUser Usuario autor) {
        ExameService.DownloadExame arquivo = service.baixar(id, autor);
        String nome = arquivo.nome().replace("\r", "").replace("\n", "").replace("\"", "");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(nome, StandardCharsets.UTF_8).build().toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(arquivo.conteudo());
    }

    @PatchMapping(value = "/exames/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ExameDTO corrigir(
        @PathVariable Long id, @Valid @RequestPart("dados") DadosExame dados,
        @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
        @CurrentUser Usuario autor) throws IOException {
        return service.corrigir(id, dados, arquivo, autor);
    }

    @PostMapping("/exames/{id}/agendamento")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ExameDTO agendar(
        @PathVariable Long id,
        @Valid @RequestBody AgendamentoExame dados,
        @CurrentUser Usuario autor) {
        return service.agendar(id, dados.disponibilizacaoEm().toInstant(), autor);
    }

    @PostMapping("/exames/{id}/suspensao")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ExameDTO suspender(
        @PathVariable Long id,
        @CurrentUser Usuario autor) {
        return service.suspender(id, autor);
    }

    @PostMapping("/exames/{id}/publicacao")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ExameDTO publicar(
        @PathVariable Long id,
        @CurrentUser Usuario autor) {
        return service.publicar(id, autor);
    }

    @PostMapping("/exames/{id}/inativacao")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ExameDTO inativar(@PathVariable Long id, @CurrentUser Usuario autor) {
        return service.inativar(id, autor);
    }

    @GetMapping("/exames/{id}/historico")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public List<EventoExame> historico(@PathVariable Long id, @CurrentUser Usuario autor) {
        return service.historico(id, autor);
    }
}
