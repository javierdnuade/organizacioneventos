package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.proyectos.organizacion_eventos.entities.EventAttendance;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;

public interface EventAttendanceRepository extends CrudRepository<EventAttendance, EventUserId> {

    @Modifying
    @Query("DELETE FROM EventAttendance ea WHERE ea.user.id = :userId")
    void deleteByUserId(@Param("userId") int userId);

}
