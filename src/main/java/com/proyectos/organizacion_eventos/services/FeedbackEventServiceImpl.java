package com.proyectos.organizacion_eventos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.dto.EventFeedbackDTO;
import com.proyectos.organizacion_eventos.dto.EventMemberFeedbackResponseDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.FeedbackEvent;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;
import com.proyectos.organizacion_eventos.exceptions.AlreadyExistsException;
import com.proyectos.organizacion_eventos.exceptions.NotFoundException;
import com.proyectos.organizacion_eventos.repositories.EventAttendanceRepository;
import com.proyectos.organizacion_eventos.repositories.EventRepository;
import com.proyectos.organizacion_eventos.repositories.FeedbackEventRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackEventServiceImpl implements FeedbackEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final FeedbackEventRepository repository;
    private final EventAttendanceRepository eventAttendanceRepository;

    @Override
    public EventMemberFeedbackResponseDTO addFeedback(int eventId, int userId, String textFeedbackUser) {

        // Buscar el evento y el usuario
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("P-501","Evento no encontrado"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("P-500","Usuario no encontrado"));

        EventUserId eventUserId = new EventUserId(eventId, userId);

        // Verificar si el usuario ha asistido al evento
        if (!eventAttendanceRepository.existsById(eventUserId)) {
            throw new NotFoundException("P-505", "El usuario no está inscrito en el evento");
        }
        // Verificar si ya existe un feedback para este evento y usuario
        if (repository.existsById(eventUserId)) {
            throw new AlreadyExistsException("P-409","Ya existe un feedback para este evento y usuario");
        }

        FeedbackEvent feedback = new FeedbackEvent(eventUserId, event, user, textFeedbackUser);
        repository.save(feedback);

        return EventMemberFeedbackResponseDTO.builder()
            .eventName(event.getName())
            .userName(user.getUsername())
            .feedback(textFeedbackUser)
            .build();
    }

    @Override
    public List<EventFeedbackDTO> getFeedbackByEvent(int eventId) {
        return repository.findFeedbackByEventId(eventId);
    }

}