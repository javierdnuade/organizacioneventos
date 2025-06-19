package com.proyectos.organizacion_eventos.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    private int id;

    @Column(unique = true)
    private String name;

    // Direcion ManyToMany con tabla intermedia "users_roles", ya que un usuario puede tener muchos roles, y muchos roles 1 usuario
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
    
    public Role () {
        this.users = new ArrayList<>();
    }
}