package com.example.crudrapido.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // Permitir acceso al login
                        .requestMatchers("/v3/api-docs/**").permitAll() // Permitir acceso a la documentación Swagger
                        .requestMatchers("/swagger-ui/**").permitAll() // Permitir acceso al UI de Swagger
                        .requestMatchers("/swagger-ui.html").permitAll() // Permitir acceso a la página principal de Swagger UI
                        .anyRequest().authenticated()) // Requiere autenticación para otros endpoints
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configuración para usar JWT
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class); // Añadir el filtro JWT
        return http.build();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(); // El filtro para manejar JWT
    }
}
