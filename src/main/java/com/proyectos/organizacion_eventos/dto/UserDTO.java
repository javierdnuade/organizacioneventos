package com.proyectos.organizacion_eventos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDTO {

    private int id;
    private String username;
    private String name;
    private String email;
    private List<String> roles;
}
