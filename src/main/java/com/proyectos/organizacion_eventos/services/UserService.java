package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.entities.User;

public interface UserService {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    User save(User user);

}
