package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.GroupUser;

public interface GroupService {

    List<GroupDTO> findAll();

    Group save(Group group);

    Optional<Group> findById(int id);

    Optional<GroupDTO> getGroupDTO(int id);

    Optional<Group> delete(int id);

    void addMember(int grupoId, int userId, boolean isLeader);

    Optional<GroupUser> removeMember(int groupId, int userId);

    boolean isLeader(int groupId, String username);

    void modifyLeader(int groupId, int userId, boolean leader);

    void addEventToGroup(int groupId, int eventId);
}
