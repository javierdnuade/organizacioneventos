package com.proyectos.organizacion_eventos.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserUpdateDTO {

    @Size(min = 4, max=30, message = "El username debe tener entre 4 y 30 caracteres")
    private String username;

    @Size(min = 2, max=40, message = "El nombre debe tener entre 4 y 40 caracteres")
    private String name;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(min = 4, max=60, message =  "La contraseña debe tener entre 4 y 60 caracteres")
    private String password;
}
