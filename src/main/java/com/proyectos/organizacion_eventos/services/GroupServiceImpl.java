package com.proyectos.organizacion_eventos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.dto.GroupMemberDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.GroupUserId;
import com.proyectos.organizacion_eventos.repositories.EventRepository;
import com.proyectos.organizacion_eventos.repositories.GroupRepository;
import com.proyectos.organizacion_eventos.repositories.GroupUserRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;


@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupDTO> findAll() {
         List<GroupMemberDTO> list = repository.fetchGroupMembers();

        Map<Integer, GroupDTO> groupMap = new HashMap<>();

        for (GroupMemberDTO row : list) {
            groupMap.computeIfAbsent(row.getId(), id ->
                GroupDTO.builder()
                    .id(id)
                    .name(row.getName())
                    .members(new ArrayList<>())
                    .build());
                groupMap.get(row.getId())
                    .getMembers()
                    .add(GroupDTO.MemberDTO.builder()
                        .name(row.getMemberName())
                        .isLeader(row.isLeader())
                        .build());
        }
        return new ArrayList<>(groupMap.values());
    }

    @Override
    public Optional<GroupDTO> getGroupDTO(int id) {
        List<GroupMemberDTO> list = repository.findGroupMembersByGroupId(id);         
    
        if (list.isEmpty()) {
            return Optional.empty();
        }

        // Tomamos el id y grupo de la primer fila ya que son todos del mismo grupo
        // y creamos el DTO con los miembros
        GroupDTO groupDTO = GroupDTO.builder()
            .id(list.get(0).getId())
            .name(list.get(0).getName())
            .members(list.stream()
                .map(row -> GroupDTO.MemberDTO.builder()
                    .name(row.getMemberName())
                    .isLeader(row.isLeader())
                    .build())
                .toList())
            .build();
        return Optional.of(groupDTO);
    }


    @Override
    public Group save(Group group) {
        return repository.save(group);
    }

    @Override
    public Optional<Group> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Group> delete(int id) {
        Optional<Group> groupOptional = repository.findById(id);
        groupOptional.ifPresent(group -> {
            repository.delete(group);
        });
        return groupOptional;
    }

    @Override
    public void addMember(int grupoId, int userId, boolean isLeader) {
        Group group = repository.findById(grupoId)
            .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        User user = userRepository.findById(userId)
            .orElseThrow( () -> new RuntimeException("Usuario no encontrado"));

        GroupUserId groupUserId = new GroupUserId(grupoId, userId);

        // Evitamos duplicados en la logica de negocio
        if (groupUserRepository.existsById(groupUserId)) {
            throw new RuntimeException("El usuario ya es miembro del grupo");
        }

        GroupUser groupUser = new GroupUser(groupUserId, group, user, isLeader);
        groupUserRepository.save(groupUser);
    }

    @Override
    public Optional<GroupUser> removeMember(int groupId, int userId) {
        GroupUserId groupUserId = new GroupUserId(groupId, userId);
        Optional<GroupUser> groupUserOptional = groupUserRepository.findById(groupUserId);
        groupUserOptional.ifPresent(groupUser -> {
            groupUserRepository.delete(groupUser);
        });

        return groupUserOptional;
    }

    @Override
    public boolean isLeader(int groupId, String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        GroupUserId groupUserId = new GroupUserId(groupId, userOpt.get().getId());
        return groupUserRepository.findById(groupUserId)
            .map(GroupUser::isLeader)
            .orElse(false);    
    }

    @Override
    public void modifyLeader(int groupId, int userId, boolean leader) {
        // Verifica si existe el grupo
        if (repository.findById(groupId).isEmpty()) {
            throw new RuntimeException("Grupo no encontrado");
        }
        // Verifica si existe el usuario
        if (userRepository.findById(userId).isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Verificamos si el usuario es miembro del grupo.

        GroupUserId groupUserId = new GroupUserId(groupId, userId);
        GroupUser groupUser = groupUserRepository.findById(groupUserId)
            .orElseThrow( () -> new RuntimeException("El usuario no es miembro del grupo"));

        groupUser.setLeader(leader);
        groupUserRepository.save(groupUser);
    }

    @Override
    public void addEventToGroup(int groupId, int eventId) {
        Group group = repository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Como es relacion bidireccional, agregamos tanto

        group.addEvent(event); // Dentro del metodo de group, se agrega el grupo al evento

        // Se guardan el evento y el grupo

        repository.save(group);        
    }

    
}