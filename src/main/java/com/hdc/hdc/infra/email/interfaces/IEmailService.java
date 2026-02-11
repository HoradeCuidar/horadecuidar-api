package com.hdc.hdc.infra.email.interfaces;

public interface IEmailService {

    void enviarEmaildeCadastro(String nome, String email, String username, String senha);
}