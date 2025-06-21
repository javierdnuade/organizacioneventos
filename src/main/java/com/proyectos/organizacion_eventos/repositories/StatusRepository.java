package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.Status;

public interface StatusRepository extends CrudRepository<Status, Integer> {

    
}
