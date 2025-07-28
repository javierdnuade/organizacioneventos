package com.proyectos.organizacion_eventos.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 4, max = 60, message = "El nombre del evento debe tener entre 4 y 60 caracteres")
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User organizer;

    private String description;

    private LocalDateTime date;
    
    private String location;

    // Muchos eventos pueden tener un estado, y un estado puede estar en muchos eventos
    @NotNull(message = "El status no debe ser nulo")
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToMany(mappedBy = "events")
    private Set<Group> groups = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackEvent> feedbacks = new ArrayList<>();;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAttendance> attendance = new ArrayList<>();

    public void addGroup(Group group) {
        groups.add(group);
        group.getEvents().add(this);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.getEvents().remove(this);
    }

    public void clearGroups() {
        // Para remover todos grupos correctamente
        for (Group group : new HashSet<>(groups)) {
            removeGroup(group);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Event other = (Event) obj;
        if (id != other.id)
            return false;
        return true;
    }

    
}
