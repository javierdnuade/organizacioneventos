package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.EventFeedbackDTO;
import com.proyectos.organizacion_eventos.dto.FeedbackRequestDTO;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.services.FeedbackEventService;
import com.proyectos.organizacion_eventos.services.UserService;
import com.proyectos.organizacion_eventos.utils.AuthOrganizerAndAdminEvent;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/feedback-events")
public class FeedbackEventController {

    @Autowired
    private AuthOrganizerAndAdminEvent authOrganizerAndAdminEvent;

    @Autowired
    private UserService userService;

    @Autowired
    private FeedbackEventService service;

    @PostMapping("addFeedback/{eventId}")
    public ResponseEntity<?> addFeedback(@PathVariable int eventId, 
                                         @RequestBody @Valid FeedbackRequestDTO textFeedbackUser) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> userOpt = userService.findByUsername(currentUsername);
        User user = userOpt.get();

        try {
            service.addFeedback(eventId, user.getId(), textFeedbackUser.getFeedback());
            return ResponseEntity.ok("Feedback agregado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("{eventId}")
    public ResponseEntity<?> getFeedbackForEvent (@PathVariable int eventId) {

        // Validacion si es admin o organizador del evento
        ResponseEntity<?> validation = authOrganizerAndAdminEvent.validationAdminOrOrganizer(eventId);
        if (validation != null) {
            return validation;
        }

        List<EventFeedbackDTO> feedbackList = service.getFeedbackByEvent(eventId);
        if (feedbackList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(feedbackList);
        
    }
}