package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.UserDTO;
import com.proyectos.organizacion_eventos.entities.Role;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.services.UserService;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping
    public ResponseEntity<List<UserDTO>> listAll() {
        List<UserDTO> usuarios = service.findAll();
        if (usuarios.isEmpty()) {
            // Si la lista está vacía, devolvemos un 204 No Content
            return ResponseEntity.noContent().build(); // 204
        }
        // Si la lista no está vacía, devolvemos un 200 OK con la lista de usuarios
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> showUser(@PathVariable int id) {
        /*
        Optional<UserDTO> user = service.findByIdDTO(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get());
        */
        return service.findByIdDTO(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> craete(@Valid @RequestBody User user, BindingResult result) {
        
        // Agregamos validación en errores en campos
        ResponseEntity<?> errors = ControllerUtils.getErrorsResponse(result);
        if (errors != null) {
            return errors;
        }

        /* Anterior forma, sin la clase de Utils creada
        if (result.hasFieldErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(err -> {
                errors.put(err.getField(), err.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        } */

        try {
            User created = service.save(user);
            // Convierte el User a UserDTO antes de devolverlo
            UserDTO dto = UserDTO.builder()
                .id(created.getId())
                .username(created.getUsername())
                .name(created.getName())
                .email(created.getEmail())
                .roles(created.getRoles().stream().map(Role::getName).toList())
            .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        Optional<User> userDelete = service.findById(id);
        if (userDelete.isPresent()) {
            User deleted = userDelete.get();
            UserDTO dto = UserDTO.builder()
                .id(deleted.getId())
                .username(deleted.getUsername())
                .name(deleted.getName())
                .email(deleted.getEmail())
                .roles(deleted.getRoles().stream().map(Role::getName).toList())
            .build();
        service.deleteById(id);
        return ResponseEntity.ok(dto);        
        }
        return ResponseEntity.notFound().build();
    }

}
