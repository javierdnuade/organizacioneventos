package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.Group;

public interface GroupRepository extends CrudRepository<Group, Integer> {
    
}