package com.proyectos.organizacion_eventos.services;

import java.util.List;

import com.proyectos.organizacion_eventos.dto.EventFeedbackDTO;

public interface FeedbackEventService {

    void addFeedback(int eventId, int userId, String feedback);

    List<EventFeedbackDTO> getFeedbackByEvent(int eventId);
}
