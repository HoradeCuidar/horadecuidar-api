package com.hdc.hdc.infra.security.configuration;

import com.hdc.hdc.infra.security.filter.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Profile("prod")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurationsProd{

    private final SecurityFilter securityFilter;

    @Autowired
    public SecurityConfigurationsProd(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // Authorization
                        .requestMatchers(HttpMethod.POST, "/api/auth/logar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/recuperacao-senha").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/resetar-senha").permitAll()

                        // Profissional da Saúde
                        .requestMatchers(HttpMethod.POST, "/api/profissional/cadastrar").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/profissional/visualizar/{id_profissional}").hasAnyRole("PROFISSIONAL_DA_SAUDE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/profissional/visualizarTodos").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/profissional/editar/{id_profissional}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/profissional/ativar/{id_profissional}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/profissional/inativar/{id_profissional}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/profissional/buscar").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/profissional/uploadFotoDePerfil/{id_profissional}").hasAnyRole("PROFISSIONAL_DA_SAUDE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
