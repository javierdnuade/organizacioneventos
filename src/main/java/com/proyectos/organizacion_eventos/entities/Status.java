package com.proyectos.organizacion_eventos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "statuses")
@Data
public class Status {

    @Id
    private int id;

    private String description;

}
