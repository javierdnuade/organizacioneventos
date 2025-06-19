package com.proyectos.organizacion_eventos.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);    

}
