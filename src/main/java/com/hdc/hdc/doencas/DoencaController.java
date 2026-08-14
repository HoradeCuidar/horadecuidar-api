package com.hdc.hdc.doencas;

import com.hdc.hdc.doencas.dto.DoencaCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doenca")
public class DoencaController {

    private final DoencaService doencaService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Doenca> save(@RequestBody DoencaCreateDto dto) {
        Doenca created = doencaService.save(dto);
        UriComponentsBuilder uri  = ServletUriComponentsBuilder.fromUriString("/api/doencas" + created.getId());

        return ResponseEntity.created(uri.build().toUri()).body(created);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<List<Doenca>> findAll() {
        return ResponseEntity.ok().body(doencaService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Doenca> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(doencaService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@RequestBody DoencaCreateDto dto, @PathVariable Long id) {
        doencaService.update(dto, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doencaService.delete(id);
        return ResponseEntity.ok().build();
    }
}
