package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.UserDTO;
import com.proyectos.organizacion_eventos.dto.UserUpdateDTO;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.services.UserService;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
            .build();
        service.deleteById(id);
        return ResponseEntity.ok(dto);        
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateDTO userUpdate, BindingResult result) {
        // Validacion de que solo se pueda actualizar el usuario que está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> userCheckOpt = service.findByUsername(currentUsername);
        if (userCheckOpt.isPresent()) {
            User userCheck = userCheckOpt.get();
            System.out.println("ID del usuario autenticado: " + userCheck.getId());
            System.out.println("ID enviado por URL: " + id);

            if (userCheck.getId() != id) {
                return ResponseEntity.status(403).body(Map.of("error", "No puedes actualizar otro usuario que no sea el tuyo"));
            }
        }

        ResponseEntity<?> errors = ControllerUtils.getErrorsResponse(result);
        if (errors != null) {
            return errors;
        }

        Optional<User> userOpt = service.update(userUpdate, id);
        if (userOpt.isPresent()) {
            User updated = userOpt.get();
            UserDTO dto = UserDTO.builder()
                .id(updated.getId())
                .username(updated.getUsername())
                .name(updated.getName())
                .email(updated.getEmail())
            .build();
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado correctamente. Vuelva a iniciar sesion",
                                            "user", dto));
        }
        return ResponseEntity.notFound().build();
    }

}
