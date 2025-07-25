package com.proyectos.organizacion_eventos.services;

import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.entities.EventAttendance;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;
import com.proyectos.organizacion_eventos.repositories.EventAttendanceRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EventAttendanceServiceImpl implements EventAttendanceService {

    private final EventAttendanceRepository repository;

    public void setAttendance(int eventId, int userId, boolean attenden) {
        
        EventUserId eventUserId = new EventUserId(eventId, userId);
        EventAttendance attendance = repository.findById(eventUserId)
            .orElseThrow( () -> new RuntimeException("Participación no encontrada"));

        if (attendance.isAttended() == attenden) {
            throw new RuntimeException("La asistencia ya está registrada con el mismo estado");
        }

        attendance.setAttended(attenden);
        repository.save(attendance);
    }
}
