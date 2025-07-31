package com.proyectos.organizacion_eventos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {

    private String code;
    private String message;

}
