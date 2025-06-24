package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.UserDTO;
import com.proyectos.organizacion_eventos.entities.User;

public interface UserService {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<UserDTO> findAll();

    User save(User user);

    Optional<User> findById(int id);

    Optional<UserDTO> findByIdDTO(int id);

    Optional<User> deleteById(int id);

}
