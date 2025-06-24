package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.entities.Group;

public interface GroupService {

    List<GroupDTO> findAll();

    Group save(Group group);

    Optional<Group> findById(int id);

    Optional<GroupDTO> getGroupDTO(int id);

    Optional<Group> delete(int id);

}
