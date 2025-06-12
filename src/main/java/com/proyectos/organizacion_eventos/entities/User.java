package com.proyectos.organizacion_eventos.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Data
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;

    private String password;

    private String name;

    @Column(unique = true)
    private String email;

    @ManyToMany
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
    private List<Role> roles;

    //Relacion de ManyToMany - Muchos usuarios pueden estar en Muchos eventos
    private List<Event> eventsAttendance;

    //Relación de OneToMany - Un usuario puede organizar muchos eventos
    private List<Event> eventsOrganizer;

    public User () {
        this.eventsAttendance = new ArrayList<>();
        this.eventsOrganizer = new ArrayList<>();
    }

}
