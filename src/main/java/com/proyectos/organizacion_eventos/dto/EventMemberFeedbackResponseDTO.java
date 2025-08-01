package com.proyectos.organizacion_eventos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventMemberFeedbackResponseDTO {

    private String eventName;
    private String userName;
    private String feedback;
}
