package com.proyectos.organizacion_eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {

    @NotBlank(message = "El texto del feedback no puede estar vacío")
    @Size(max = 300, message = "El feedback no puede exceder los 500 caracteres")
    private String feedback;
}
