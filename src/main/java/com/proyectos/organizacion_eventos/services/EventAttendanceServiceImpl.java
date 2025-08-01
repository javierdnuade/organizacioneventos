package com.proyectos.organizacion_eventos.services;

import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.dto.EventAttendanceResponseDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.EventAttendance;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;
import com.proyectos.organizacion_eventos.exceptions.AlreadyExistsException;
import com.proyectos.organizacion_eventos.exceptions.NotFoundException;
import com.proyectos.organizacion_eventos.repositories.EventAttendanceRepository;
import com.proyectos.organizacion_eventos.repositories.EventRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EventAttendanceServiceImpl implements EventAttendanceService {

    private final EventAttendanceRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventAttendanceResponseDTO setAttendance(int eventId, int userId, boolean attenden) {
        
        User user = userRepository.findById(userId)
            .orElseThrow( () -> new NotFoundException("P-500","Usuario no encontrado"));

        Event event = eventRepository.findById(userId)
            .orElseThrow( () -> new NotFoundException("P-501","Evento no encontrado"));

        EventUserId eventUserId = new EventUserId(eventId, userId);
        EventAttendance attendance = repository.findById(eventUserId)
            .orElseThrow( () -> new NotFoundException("P-512","Participación no encontrada"));

        if (attendance.isAttended() == attenden) {
            throw new AlreadyExistsException("P-407","La asistencia ya está registrada con el mismo estado");
        }

        attendance.setAttended(attenden);
        repository.save(attendance);
        return EventAttendanceResponseDTO.builder()
            .eventName(event.getName())
            .userName(user.getUsername())
            .attended(attenden)
            .build();
    }
}
