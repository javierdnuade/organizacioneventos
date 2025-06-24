package com.proyectos.organizacion_eventos.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "El organizador del evento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    private String description;

    private LocalDateTime date;
    
    private String location;

    // Muchos eventos pueden tener un estado, y un estado puede estar en muchos eventos
    @NotNull(message = "El status no debe ser nulo")
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToMany
    @JoinTable(
        name = "event_groups",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "group_id"}))
    private Set<Group> groups;

    @OneToMany(mappedBy = "event")
    private List<FeedbackEvent> feedbacks;
    
    @OneToMany(mappedBy = "event")
    private List<EventAttendance> attendance;
}
