package com.proyectos.organizacion_eventos.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.services.EventAttendanceService;
import com.proyectos.organizacion_eventos.services.EventService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/event-attendance")
@RequiredArgsConstructor
public class EventAttendanceController {


    private final EventService eventService;

    private final EventAttendanceService service;

    @PutMapping("/{eventId}/users/{userId}")
    public ResponseEntity<?> setAttendance (@PathVariable int eventId, @PathVariable int userId) {
        
        Optional<Event> eventOpt = eventService.findById(eventId);

        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Evento no encontrado"));
        }

        Event event = eventOpt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        if (!(currentUsername.equals(event.getOrganizer().getUsername()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Solo el organizador puede modificar la asistencia"));
        }

        try {
            service.setAttendance(eventId, userId, true);
            return ResponseEntity.ok(Map.of("message", "Asistencia registrada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
