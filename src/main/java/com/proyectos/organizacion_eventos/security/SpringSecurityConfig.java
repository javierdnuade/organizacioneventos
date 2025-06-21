package com.proyectos.organizacion_eventos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF (Cross-Site Request Forgery) para simplificar la configuración
            .cors(cors -> cors.disable()) // Deshabilita CORS (Cross-Origin Resource Sharing) para simplificar la configuración
            .authorizeHttpRequests(auth -> auth // Configura las reglas de autorización
                .anyRequest().permitAll() // Permite TODO sin autenticación
            );
        return http.build(); 
    }
}