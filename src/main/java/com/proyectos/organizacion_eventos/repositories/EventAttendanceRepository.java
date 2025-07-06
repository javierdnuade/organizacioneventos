package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.EventAttendance;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;

public interface EventAttendanceRepository extends CrudRepository<EventAttendance, EventUserId> {


}
