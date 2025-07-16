package com.proyectos.organizacion_eventos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.proyectos.organizacion_eventos.dto.EventFeedbackDTO;
import com.proyectos.organizacion_eventos.entities.FeedbackEvent;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;

public interface FeedbackEventRepository extends CrudRepository<FeedbackEvent, EventUserId> {

    @Query("""
            SELECT new com.proyectos.organizacion_eventos.dto.EventFeedbackDTO(
            fb.user.name, fb.feedback)
            FROM FeedbackEvent fb
            WHERE fb.event.id = :eventId
            """)
            List<EventFeedbackDTO> findFeedbackByEventId(int eventId);

    @Modifying
    @Query("DELETE FROM FeedbackEvent fb WHERE fb.user.id = :userId")
    void deleteByUserId(@Param("userId") int userId);
}
