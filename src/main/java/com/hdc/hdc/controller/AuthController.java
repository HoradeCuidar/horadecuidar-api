package com.hdc.hdc.controller;

import com.hdc.hdc.dto.auth.AuthDTO;
import com.hdc.hdc.dto.response.AuthResponseDTO;
import com.hdc.hdc.service.AuthService;
import com.hdc.hdc.service.interfaces.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController{

    private final IAuthService authService;

    @Autowired
    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/logar")
    public AuthResponseDTO logar(@RequestBody AuthDTO usuario){
        return authService.logar(usuario);
    }
}