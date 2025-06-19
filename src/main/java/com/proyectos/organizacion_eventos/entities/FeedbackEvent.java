package com.proyectos.organizacion_eventos.entities;

import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "event_feedback")
@AllArgsConstructor
@Data
public class FeedbackEvent {

    @EmbeddedId
    private EventUserId id;

    @ManyToOne
    @MapsId("eventId") // Hace referencia a como esta el atributo en la clase Embeddable
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    private String feeedback;

    public FeedbackEvent() {
        this.feeedback = "";
    }
}
