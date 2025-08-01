package com.proyectos.organizacion_eventos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupEventResponseDTO {

    private String groupName;
    private String eventName;
}
