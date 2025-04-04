package com.example.crudrapido.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(auth -> auth
                .anyRequest().permitAll()  // Permitir todo el tráfico sin autenticación
            )
            .csrf(AbstractHttpConfigurer::disable);  // Deshabilitar CSRF si es necesario
        return http.build();
    }
}
