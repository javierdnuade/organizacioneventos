package com.proyectos.organizacion_eventos.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private int id;
    private String name;
    private String description;
    private LocalDateTime date;
    private String location;
    private String status; // o StatusDTO si quieres más detalle
    private String organizer; // o UserDTO si quieres más detalle

    // Lista de asistentes
    private List<String> attendance;
}

