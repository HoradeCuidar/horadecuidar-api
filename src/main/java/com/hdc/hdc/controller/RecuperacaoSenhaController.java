package com.hdc.hdc.controller;

import com.hdc.hdc.dto.create.ResetPasswordCreateDto;
import com.hdc.hdc.dto.response.ResetPasswordResponseDto;
import com.hdc.hdc.service.RecuperacaoSenhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping("/recuperacao-senha")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> recuperacaoSenha(@RequestBody String email) {
        this.recuperacaoSenhaService.solicitacao(email);
        return new ResponseEntity<>("Se o email estiver cadastrado, você receberá instruções na sua caixa de email.", HttpStatus.ACCEPTED);
    }

    @PostMapping("/resetar-senha")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResetPasswordResponseDto> resetarSenha(@RequestBody ResetPasswordCreateDto dto) {
        ResetPasswordResponseDto result = this.recuperacaoSenhaService.resetarSenha(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
