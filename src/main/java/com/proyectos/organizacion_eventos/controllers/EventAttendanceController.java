package com.proyectos.organizacion_eventos.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.EventAttendanceResponseDTO;
import com.proyectos.organizacion_eventos.services.EventAttendanceService;
import com.proyectos.organizacion_eventos.utils.AuthOrganizerAndAdminEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/event-attendance")
@RequiredArgsConstructor
public class EventAttendanceController {

    private final EventAttendanceService service;
    private final AuthOrganizerAndAdminEvent authOrganizerAndAdminEvent;

    @PutMapping("/{eventId}/users/{userId}")
    public ResponseEntity<?> setAttendance (@PathVariable int eventId, @PathVariable int userId) {

        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(eventId);
        if (validation != null) {
            return validation;
        }
        
        EventAttendanceResponseDTO response = service.setAttendance(eventId, userId, true);
        return ResponseEntity.ok(response);
        
    }
}
