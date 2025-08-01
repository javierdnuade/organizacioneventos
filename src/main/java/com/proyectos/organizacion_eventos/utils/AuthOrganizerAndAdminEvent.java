package com.proyectos.organizacion_eventos.utils;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.proyectos.organizacion_eventos.services.EventService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthOrganizerAndAdminEvent {

    private final EventService eventService;

    public ResponseEntity<?> validationAdminOrOrganizer(int eventId) {
        // Revisamos rol del que maneja la solicitud
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Verificamos is es ADMIN del sistema
        boolean admin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Verificamos si es organizador del evento
        Boolean organizer = eventService.isOrganizer(eventId, currentUsername);

        if (!admin && (!organizer || organizer == null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Solo el organizador o admin pueden hacer cambios en el evento"));
        }    
        return null;
    }
}
