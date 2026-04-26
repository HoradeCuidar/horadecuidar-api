package com.hdc.hdc.auth.autenticacao;

import com.hdc.hdc.auth.autenticacao.dto.AuthDTO;
import com.hdc.hdc.auth.autenticacao.dto.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController{

    private final AuthService authService;

    @Autowired
    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/logar")
    public AuthResponseDTO logar(@RequestBody AuthDTO usuario){
        return authService.logar(usuario);
    }
}