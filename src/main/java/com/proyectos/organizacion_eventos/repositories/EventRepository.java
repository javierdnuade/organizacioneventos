package com.proyectos.organizacion_eventos.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {

    // Falta crear la logica en el Service para que se pueda usar este metodo
    List<Event> findByStatus(int statudId);

}
