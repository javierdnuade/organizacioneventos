package com.proyectos.organizacion_eventos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyectos.organizacion_eventos.dto.EventParticipantDTO;
import com.proyectos.organizacion_eventos.entities.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {

    // Falta crear la logica en el Service para que se pueda usar este metodo
    List<Event> findByStatusId(int statusId);


    @Query("""
            SELECT new com.proyectos.organizacion_eventos.dto.EventParticipantDTO(
                att.user.id, att.user.name, att.attended
            )
            FROM EventAttendance att
            WHERE att.event.id = :eventId
            """)
    List<EventParticipantDTO> findParticipantsByEventId(@Param("eventId") int eventId);

    @Query("""
            SELECT new com.proyectos.organizacion_eventos.dto.EventParticipantDTO(
                att.user.id, att.user.name, att.attended
            )
            FROM EventAttendance att
            WHERE att.event.id = :eventId AND att.attended = :attended
            """)
    List<EventParticipantDTO> findParticipantsByAttended(@Param("eventId") int eventId, @Param("attended") boolean attended); 
}