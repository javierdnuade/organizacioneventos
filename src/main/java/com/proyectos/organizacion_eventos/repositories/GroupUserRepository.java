package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.embeddable.GroupUserId;

public interface GroupUserRepository extends CrudRepository<GroupUser, GroupUserId> {

    @Modifying
    @Query("DELETE FROM GroupUser gu WHERE gu.user.id = :userId")
    void deleteByUserId(@Param("userId") int userId);

}
