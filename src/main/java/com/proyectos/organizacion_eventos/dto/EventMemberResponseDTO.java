package com.proyectos.organizacion_eventos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventMemberResponseDTO {

    private String userName;
    private String eventName;
    private String eventDate;
    private String location;
}
