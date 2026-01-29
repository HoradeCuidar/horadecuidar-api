package com.hdc.hdc.service;

import com.hdc.hdc.dto.auth.AuthDTO;
import com.hdc.hdc.dto.response.AuthResponseDTO;
import com.hdc.hdc.infra.security.service.TokenService;
import com.hdc.hdc.model.Usuario;
import com.hdc.hdc.service.interfaces.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public AuthResponseDTO logar(AuthDTO usuario){

        var usuarioSenha = new UsernamePasswordAuthenticationToken(usuario.getUsername(),usuario.getSenha());
        var auth = authenticationManager.authenticate(usuarioSenha);
        Usuario usuarioLogado = (Usuario) auth.getPrincipal();
        var token = tokenService.generatedToken(usuarioLogado);
        return new AuthResponseDTO(usuarioLogado.getId(), token, usuarioLogado.getRole());
    }
}