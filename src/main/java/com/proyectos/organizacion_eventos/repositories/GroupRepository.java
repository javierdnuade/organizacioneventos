package com.proyectos.organizacion_eventos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.proyectos.organizacion_eventos.dto.GroupMemberDTO;
import com.proyectos.organizacion_eventos.entities.Group;

public interface GroupRepository extends CrudRepository<Group, Integer> {
    @Query("""
    SELECT new com.proyectos.organizacion_eventos.dto.GroupMemberDTO(g.id, g.name, u.name, gu.isLeader)
    FROM GroupUser gu
    JOIN gu.group g
    JOIN gu.user u
    ORDER BY g.id
    """)
    List<GroupMemberDTO> fetchGroupMembers();

    @Query("""
    SELECT new com.proyectos.organizacion_eventos.dto.GroupMemberDTO(g.id, g.name, gu.user.name, gu.isLeader)
    FROM GroupUser gu
    JOIN gu.group g
    WHERE g.id = :groupId
    """)
    List<GroupMemberDTO> findGroupMembersByGroupId(@Param("groupId") int groupId);
}