package com.proyectos.organizacion_eventos.repositories;

import java.util.Optional;

import com.proyectos.organizacion_eventos.entities.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Integer> {

    boolean existsByName(String name);

    Optional<Role> findByName(String name);

}
