package com.proyectos.organizacion_eventos.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.embeddable.GroupUserId;

public interface GroupUserRepository extends CrudRepository<GroupUser, GroupUserId> {

}
