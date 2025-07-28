package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.dto.EventUpdateDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.EventAttendance;

public interface EventService {

    List<EventDTO> findAll();

    Event save(Event event);

    Optional<EventDTO> getEventDTO(int id);

    Optional<Event> findById(int id);

    List<EventDTO> findByStatus(String status);

    Optional<Event> delete(int id);

    Optional<String> addMember(int eventId, int userId);

    Optional<EventAttendance> removeMember(int eventId, int userId);

    Optional<EventDTO> getParticipationForEventDTO(int id, boolean attendance);

    Boolean isOrganizer(int eventId, String username);

    Optional<Event> update (EventUpdateDTO event, int id);
}
