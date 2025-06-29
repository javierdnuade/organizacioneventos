package com.proyectos.organizacion_eventos.entities.embeddable;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class GroupUserId implements Serializable {

    private int groupId;
    private int userId;

}
