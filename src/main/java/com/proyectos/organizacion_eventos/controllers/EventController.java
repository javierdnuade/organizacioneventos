package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.dto.EventMemberResponseDTO;
import com.proyectos.organizacion_eventos.dto.EventUpdateDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.services.EventService;
import com.proyectos.organizacion_eventos.utils.AuthOrganizerAndAdminEvent;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final AuthOrganizerAndAdminEvent authOrganizerAndAdminEvent;
    
    private final EventService service;

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
                .organizer(event.getOrganizer() != null ? event.getOrganizer().getName() : null)
                .attendance(List.of()) // Asumiendo que no se necesita asistencia al eliminar
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

        EventMemberResponseDTO memberResponse = service.addMember(id, userId);
        return ResponseEntity.ok(memberResponse);
    }
    
    @DeleteMapping("/{id}/removeMember/{userId}")
    public ResponseEntity<?> removeMember (
        @PathVariable int id,
        @PathVariable int userId) {
        
        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(id);
        if (validation != null) {
            return validation;
        }

        EventMemberResponseDTO memberResponse  = service.removeMember(id, userId);
        return ResponseEntity.ok(memberResponse);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody EventUpdateDTO eventUpdate, BindingResult result, @PathVariable int id) {
        
        // Validamos que solo el organizador o administrador pueda actualizar el evento
        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(id);
        if (validation != null) {
            return validation;
        }

        // Validación de errores en campos
        ResponseEntity<?> errors = ControllerUtils.getErrorsResponse(result);
        if (errors != null) {
            return errors;
        }

        Optional<Event> eventOpt = service.update(eventUpdate, id);
        if (eventOpt.isPresent()) {
            Event updatedEvent = eventOpt.get();
            EventDTO dto = EventDTO.builder()
                .id(updatedEvent.getId())
                .name(updatedEvent.getName())
                .description(updatedEvent.getDescription())
                .date(updatedEvent.getDate())
                .location(updatedEvent.getLocation())
                .status(updatedEvent.getStatus().getDescription())
                .organizer(updatedEvent.getOrganizer() != null ? updatedEvent.getOrganizer().getName() : null)
                .attendance(List.of())
            .build();
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
    
}
