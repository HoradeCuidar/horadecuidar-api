package com.hdc.hdc.infra.security.service;

import com.hdc.hdc.auth.autenticacao.dto.UsuarioRegisterDTO;
import com.hdc.hdc.usuarios.Administrador;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.dto.UsuarioDTO;
import com.hdc.hdc.usuarios.enums.Genero;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.usuarios.enums.Status;
import com.hdc.hdc.usuarios.UsuarioRepository;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
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

    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("ID", "Usuário não encontrado com o id informado."));

        return this.toDTO(usuario);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getRole(),
                usuario.getStatus(),
                usuario.getTelefone(),
                usuario.getGenero(),
                usuario.getEmail(),
                usuario.getFotoDePerfil()
        );
    }
}