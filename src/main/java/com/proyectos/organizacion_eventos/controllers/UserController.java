package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.UserDTO;
import com.proyectos.organizacion_eventos.entities.Role;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.services.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

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
