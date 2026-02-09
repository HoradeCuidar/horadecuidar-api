package com.hdc.hdc.controller;

import com.hdc.hdc.dto.create.DoencaCreateDto;
import com.hdc.hdc.model.Doenca;
import com.hdc.hdc.service.DoencaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping(name = "/api/doencas")
@RequiredArgsConstructor
public class DoencaController {

    private final DoencaService doencaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Doenca> save(@RequestBody DoencaCreateDto dto) {
        Doenca created = doencaService.save(dto);
        UriComponentsBuilder uri  = ServletUriComponentsBuilder.fromUriString("/api/doencas" + created.getId());

        return ResponseEntity.created(uri.build().toUri()).body(created);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Doenca>> findAll() {
        return ResponseEntity.ok().body(doencaService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Doenca> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(doencaService.getById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@RequestBody DoencaCreateDto dto, @PathVariable Long id) {
        doencaService.update(dto, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doencaService.delete(id);
        return ResponseEntity.ok().build();
    }
}
