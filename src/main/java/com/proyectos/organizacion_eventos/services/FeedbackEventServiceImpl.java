package com.proyectos.organizacion_eventos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.dto.EventFeedbackDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.FeedbackEvent;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;
import com.proyectos.organizacion_eventos.repositories.EventAttendanceRepository;
import com.proyectos.organizacion_eventos.repositories.EventRepository;
import com.proyectos.organizacion_eventos.repositories.FeedbackEventRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

@Service
public class FeedbackEventServiceImpl implements FeedbackEventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackEventRepository repository;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Override
    public void addFeedback(int eventId, int userId, String textFeedbackUser) {

        // Buscar el evento y el usuario
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EventUserId eventUserId = new EventUserId(eventId, userId);

        // Verificar si el usuario ha asistido al evento
        if (!eventAttendanceRepository.existsById(eventUserId)) {
            throw new RuntimeException("El usuario no ha asistido al evento");
        }
        // Verificar si ya existe un feedback para este evento y usuario
        if (repository.existsById(eventUserId)) {
            throw new RuntimeException("Ya existe un feedback para este evento y usuario");
        }
        FeedbackEvent feedback = new FeedbackEvent(eventUserId, event, user, textFeedbackUser);

        repository.save(feedback);
    }

    @Override
    public List<EventFeedbackDTO> getFeedbackByEvent(int eventId) {
        return repository.findFeedbackByEventId(eventId);
    }

}