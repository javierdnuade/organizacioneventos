package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.entities.Event;

public interface EventService {

    List<EventDTO> findAll();

    Event save(Event event);

    Optional<EventDTO> getEventDTO(int id);

    Optional<Event> findById(int id);

    List<EventDTO> findByStatus(String status);

    Optional<Event> delete(int id);

    void addMember(int eventId, int userId, String usernameAuth);

    void removeMember(int eventId, int userId, String usernameAuth);
}
