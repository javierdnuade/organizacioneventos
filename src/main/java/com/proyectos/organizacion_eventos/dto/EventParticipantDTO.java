package com.proyectos.organizacion_eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipantDTO {

    private int userId;
    private String username;
    private boolean attended;

}
