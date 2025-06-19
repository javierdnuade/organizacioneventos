package com.proyectos.organizacion_eventos.entities.embeddable;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Data
public class EventUserId implements Serializable {

    private int eventId;
    private int userId;

}
