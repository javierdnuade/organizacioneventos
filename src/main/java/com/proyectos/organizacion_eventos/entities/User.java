package com.proyectos.organizacion_eventos.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Entity
@Table(name = "users")
@AllArgsConstructor
@Data
@Builder
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max=30)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max=60)
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 60)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(unique = true, nullable = false)
    private String email;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
    private Set<Role> roles;

    //Relacion de ManyToMany (pero con tabla intermedia) - Muchos usuarios pueden estar en Muchos eventos
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAttendance> eventsAttendance;

    //Relación de OneToMany - Un usuario puede organizar muchos eventos
    @OneToMany
    @JoinColumn(name = "organizer_id")
    private List<Event> eventsOrganizer;

    //Relacion de ManyToMany pero con tabla intermedia enriquecida - Muchos usuarios pueden estar en Muchos grupos
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupUser> groups;

    //Relacion de ManyToMany (pero con tabla intermedia) - Muchos usuarios pueden tener muchos feedbacks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackEvent> feedbacks;

    // No es un campo de la BD, es una bandera para cuando se crea el usuario, se asigna si es admin o no
    @Transient // Transient es para que no se persista
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;    

    public User () {
        this.eventsAttendance = new ArrayList<>();
        this.eventsOrganizer = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.roles = new HashSet<>();
        this.feedbacks = new ArrayList<>();
    }

}
