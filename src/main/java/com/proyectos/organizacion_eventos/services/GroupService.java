package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.dto.GroupEventResponseDTO;
import com.proyectos.organizacion_eventos.dto.GroupMemberResponseDTO;
import com.proyectos.organizacion_eventos.entities.Group;

public interface GroupService {

    List<GroupDTO> findAll();

    Group save(Group group);

    Optional<Group> findById(int id);

    Optional<GroupDTO> getGroupDTO(int id);

    GroupDTO delete(int id);

    GroupMemberResponseDTO addMember(int grupoId, int userId, boolean isLeader);

    GroupMemberResponseDTO removeMember(int groupId, int userId);

    boolean isLeader(int groupId, String username);

    GroupMemberResponseDTO modifyLeader(int groupId, int userId, boolean leader);

    GroupEventResponseDTO addEventToGroup(int groupId, int eventId);

    GroupEventResponseDTO removeEventFromGroup(int groupId, int eventId);
}
