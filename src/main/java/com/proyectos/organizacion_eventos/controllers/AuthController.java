package com.proyectos.organizacion_eventos.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.UserDTO;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.repositories.UserRepository;
import com.proyectos.organizacion_eventos.security.JwtUtil;
import com.proyectos.organizacion_eventos.services.UserService;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        
        // Agregamos validación en errores en campos
        ResponseEntity<?> errors = ControllerUtils.getErrorsResponse(result);
        if (errors != null) {
            return errors;
        }

        try {
            User created = userService.save(user);
            // Convierte el User a UserDTO antes de devolverlo
            UserDTO dto = UserDTO.builder()
                .id(created.getId())
                .username(created.getUsername())
                .name(created.getName())
                .email(created.getEmail())
            .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
        }
        
        User userDb = userOptional.get();
        if (!passwordEncoder.matches(user.getPassword(), userDb.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        } 

        Map<String, Object> response = new HashMap<>();
        String token = jwtUtil.createToken(userDb.getUsername());
        Date expirationDate = new Date(System.currentTimeMillis() + 86400000);
        
        response.put("token", token);
        response.put("expiresAt", expirationDate);

        return ResponseEntity.ok(response);
    }
}
