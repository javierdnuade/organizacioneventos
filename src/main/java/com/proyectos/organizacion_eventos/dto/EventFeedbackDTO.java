package com.proyectos.organizacion_eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFeedbackDTO {

    private String name; 
    private String feedback;
}
