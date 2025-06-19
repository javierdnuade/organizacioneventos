package com.proyectos.organizacion_eventos.entities;

import com.proyectos.organizacion_eventos.entities.embeddable.GroupUserId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;


// Clase intermedia para los grupos y usuarios

@Data
@AllArgsConstructor
@Entity
@Table(name = "group_user")
public class GroupUser {

    @EmbeddedId
    private GroupUserId id;

    @ManyToOne
    @MapsId("groupId") // Hace referencia a como esta el atributo en la clase Embeddable
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("userId") // Hace referencia a como esta el atributo en la clase Embeddable
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_leader", nullable = false)
    private boolean isLeader;

    public GroupUser() {
        this.isLeader = false; // Por defecto, un usuario no es lider
    }
}
