package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.entities.Event;

public interface EventService {

    List<EventDTO> findAll();

    Event save(Event event);

    Optional<Event> deleteById(int id);

    Optional<EventDTO> getEventDTO(int id);

    Optional<Event> findById(int id);

    List<EventDTO> findByStatusId(int statusId);

    Optional<Event> delete(int id);
}
