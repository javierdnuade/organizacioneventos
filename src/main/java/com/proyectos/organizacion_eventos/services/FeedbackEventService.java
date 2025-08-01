package com.proyectos.organizacion_eventos.services;

import java.util.List;

import com.proyectos.organizacion_eventos.dto.EventFeedbackDTO;
import com.proyectos.organizacion_eventos.dto.EventMemberFeedbackResponseDTO;

public interface FeedbackEventService {

    EventMemberFeedbackResponseDTO addFeedback(int eventId, int userId, String feedback);

    List<EventFeedbackDTO> getFeedbackByEvent(int eventId);
}
