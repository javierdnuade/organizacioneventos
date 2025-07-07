package com.proyectos.organizacion_eventos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {

    @NotBlank(message = "El texto del feedback no puede estar vacío")
    private String feedback;
}
