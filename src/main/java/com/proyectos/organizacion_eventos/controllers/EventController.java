package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.EventAttendance;
import com.proyectos.organizacion_eventos.services.EventService;
import com.proyectos.organizacion_eventos.utils.AuthOrganizerAndAdminEvent;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private AuthOrganizerAndAdminEvent authOrganizerAndAdminEvent;

    @Autowired
    private EventService service;

    @GetMapping
    public ResponseEntity<List<EventDTO>> list() {
        List<EventDTO> events = service.findAll();
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> showEvent (@PathVariable int id) {
        return service.getEventDTO(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build()); 

    }

    @PostMapping
    public ResponseEntity<?> create (@Valid @RequestBody Event event, BindingResult result) {
        
        // Agregamos validación en errores en campos
        ResponseEntity<?> errors = ControllerUtils.getErrorsResponse(result);
        if (errors != null) {
            return errors;
        }

        try {
            Event created = service.save(event);
            // Convertimos el evento a DTO para imprimirlo
            EventDTO dto = EventDTO.builder()
                .id(created.getId())
                .name(created.getName())
                .description(created.getDescription())
                .date(created.getDate())
                .location(created.getLocation())
                .status(created.getStatus().getDescription())
                .organizer(created.getOrganizer().getName())
            .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventDTO>> listByStatus(@PathVariable String status) {
        List<EventDTO> events = service.findByStatus(status);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent (@PathVariable int id) {
        Optional<Event> eventOptional = service.findById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            EventDTO eventDTO = EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .status(event.getStatus().getDescription())
                .organizer(event.getOrganizer().getName())
            .build();
            service.delete(id);
            return ResponseEntity.ok(eventDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/addMember/{userId}")    
    public ResponseEntity<?> addMember(
            @PathVariable int id,
            @PathVariable int userId) {

        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(id);
        if (validation != null) {
            return validation;
        }

        Optional<String> errorResult = service.addMember(id, userId);
        if (errorResult.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", errorResult.get()));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Usuario agregado al evento correctamente"));
    }

    @DeleteMapping("/{id}/removeMember/{userId}")
    public ResponseEntity<?> removeMember (
        @PathVariable int id,
        @PathVariable int userId) {
        
        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(id);
        if (validation != null) {
            return validation;
        }

        Optional <EventAttendance> result = service.removeMember(id, userId);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "El usuario no estaba inscrito en el evento"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Usuario removido del evento correctamente"));
    }


    @GetMapping("/{id}/attendance")
    public ResponseEntity<?> showEventAttendance (@PathVariable int id, @RequestParam Boolean attended) {

        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(id);
        if (validation != null) {
            return validation;
        }

        return service.getParticipationForEventDTO(id, attended)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build()); 

    }
    
}
