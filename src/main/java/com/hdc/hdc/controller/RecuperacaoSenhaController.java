package com.hdc.hdc.controller;

import com.hdc.hdc.dto.create.ResetPasswordCreateDto;
import com.hdc.hdc.dto.create.SolicitarRecuperacaoDto;
import com.hdc.hdc.service.RecuperacaoSenhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping("/recuperacao-senha")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Map<String, String>> recuperacaoSenha(@RequestBody SolicitarRecuperacaoDto email) {
        this.recuperacaoSenhaService.solicitarRecuperacaoSenha(email);
        return ResponseEntity.accepted().body(
                Map.of("message", "Se o email estiver cadastrado, você receberá instruções na sua caixa de email.")
        );
    }

    @PostMapping("/resetar-senha")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> resetarSenha(@RequestBody ResetPasswordCreateDto dto) {
        recuperacaoSenhaService.resetarSenha(dto);
        return ResponseEntity.ok().body(
                Map.of("message", "Senha redefinida com sucesso.")
        );
    }
}
