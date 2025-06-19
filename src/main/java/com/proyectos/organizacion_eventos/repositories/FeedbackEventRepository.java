package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.FeedbackEvent;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;

public interface FeedbackEventRepository extends CrudRepository<FeedbackEvent, EventUserId> {

}
