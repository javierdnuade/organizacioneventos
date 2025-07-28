package com.proyectos.organizacion_eventos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateDTO {

    @Size(min = 4, max = 60, message = "El nombre del evento debe tener entre 4 y 60 caracteres")
    private String name;

    @Size(max = 120, message = "La descripción del evento no puede exceder los 120 caracteres")
    private String description;

    @Future(message = "La fecha del evento debe ser en el futuro")
    private LocalDateTime date;

    @Size(max = 60, message = "La ubicación del evento no puede exceder los 60 caracteres")
    private String location;

    @Min(value = 0, message = "El status debe ser 0, 1 o 2")
    @Max(value = 2, message = "El status debe ser 0, 1 o 2")
    private Integer status; // o StatusDTO si quieres más detalle
}
