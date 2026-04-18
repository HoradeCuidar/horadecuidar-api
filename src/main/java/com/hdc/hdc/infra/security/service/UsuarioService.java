package com.hdc.hdc.infra.security.service;

import com.hdc.hdc.dto.auth.UsuarioRegisterDTO;
import com.hdc.hdc.model.Administrador;
import com.hdc.hdc.model.Usuario;
import com.hdc.hdc.model.enums.Genero;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import com.hdc.hdc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public void save(UsuarioRegisterDTO dto) {
        if (dto.role() == Role.ADMIN) {
            Administrador admin = new Administrador();
            admin.setNome("Administrador Root");
            admin.setUsername(dto.username());
            admin.setEmail(dto.email());
            admin.setSenha(passwordEncoder.encode(dto.senha()));
            admin.setDataDeNascimento(LocalDate.of(2000, 1, 1));
            admin.setGenero(Genero.OUTRO);
            admin.setStatus(Status.ATIVO);
            admin.setRole(Role.ADMIN);
            
            usuarioRepository.save(admin);
        } else {
            throw new UnsupportedOperationException("Registration for this role is not implemented here.");
        }
    }
}