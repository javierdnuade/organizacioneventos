package com.proyectos.organizacion_eventos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.dto.GroupEventResponseDTO;
import com.proyectos.organizacion_eventos.dto.GroupMemberDTO;
import com.proyectos.organizacion_eventos.dto.GroupMemberResponseDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.GroupUserId;
import com.proyectos.organizacion_eventos.exceptions.AlreadyExistsException;
import com.proyectos.organizacion_eventos.exceptions.NotFoundException;
import com.proyectos.organizacion_eventos.repositories.EventRepository;
import com.proyectos.organizacion_eventos.repositories.GroupRepository;
import com.proyectos.organizacion_eventos.repositories.GroupUserRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final EventRepository eventRepository;

    private final GroupUserRepository groupUserRepository;

    private final UserRepository userRepository;

    private final GroupRepository repository;

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
                        .isLeader(Boolean.TRUE.equals(row.getIsLeader()))
                    .build());
        }
        return new ArrayList<>(groupMap.values());
    }

    @Override
    public Optional<GroupDTO> getGroupDTO(int id) {
        List<GroupMemberDTO> list = repository.findGroupMembersByGroupId(id);         

        // 🔴 Si no existe ningún grupo con ese ID, entonces sí devolvés vacío.
        if (list.isEmpty()) {
            return Optional.empty(); // <-- mantiene el 404 para grupos inexistentes
        }

        // 🟢 Pero si el grupo existe, aunque no tenga usuarios, la lista tendrá al menos una fila con memberName = null
        GroupDTO groupDTO = GroupDTO.builder()
            .id(list.get(0).getId())
            .name(list.get(0).getName())
            .members(
                list.stream()
                    .filter(row -> row.getMemberName() != null) // solo miembros válidos
                    .map(row -> GroupDTO.MemberDTO.builder()
                        .name(row.getMemberName())
                        .isLeader(Boolean.TRUE.equals(row.getIsLeader()))
                        .build())
                    .toList()
            )
            .build();

        return Optional.of(groupDTO);
    }


    @Override
    @Transactional
    public Group save(Group group) {
        return repository.save(group);
    }

    @Override
    public Optional<Group> findById(int id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public GroupDTO delete(int id) {
        Group group = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("P-502","Grupo no encontrado"));


        repository.delete(group);
        return GroupDTO.builder()
            .id(group.getId())
            .name(group.getName())
            .members(new ArrayList<>()) // No hay miembros al eliminar el grupo
            .build();
    }

    @Override
    @Transactional
    public GroupMemberResponseDTO addMember(int grupoId, int userId, boolean isLeader) {
        Group group = repository.findById(grupoId)
            .orElseThrow(() -> new NotFoundException("P-501","Grupo no encontrado"));

        User user = userRepository.findById(userId)
            .orElseThrow( () -> new NotFoundException("P-500","Usuario no encontrado"));

        GroupUserId groupUserId = new GroupUserId(grupoId, userId);

        // Evitamos duplicados en la logica de negocio
        if (groupUserRepository.existsById(groupUserId)) {
            throw new AlreadyExistsException("P-400","El usuario ya es miembro del grupo");
        }

        GroupUser groupUser = new GroupUser(groupUserId, group, user, isLeader);
        groupUserRepository.save(groupUser);
        return GroupMemberResponseDTO.builder()
            .groupName(group.getName())
            .userName(user.getName())
            .isLeader(isLeader)
        .build();
    }

    @Override
    @Transactional
    public GroupMemberResponseDTO removeMember(int groupId, int userId) {

        Group group = repository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("P-501","Grupo no encontrado"));

        User user = userRepository.findById(userId)
            .orElseThrow( () -> new NotFoundException("P-500","Usuario no encontrado"));

        GroupUserId groupUserId = new GroupUserId(groupId, userId);
        GroupUser groupUser = groupUserRepository.findById(groupUserId)
            .orElseThrow( () -> new NotFoundException("P-505","Usuario no encontrado en el grupo"));
        
        groupUserRepository.delete(groupUser);
        return GroupMemberResponseDTO.builder()
            .groupName(user.getName())
            .userName(group.getName())
            .isLeader(groupUser.isLeader())
        .build();
    }

    @Override
    public boolean isLeader(int groupId, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow( () -> new NotFoundException("P-500","Usuario no encontrado"));
        
        GroupUserId groupUserId = new GroupUserId(groupId, user.getId());
        return groupUserRepository.findById(groupUserId)
            .map(GroupUser::isLeader)
            .orElse(false);    
    }

    @Override
    @Transactional
    public GroupMemberResponseDTO modifyLeader(int groupId, int userId, boolean leader) {

        Group group = repository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("P-501","Grupo no encontrado"));

        User user = userRepository.findById(userId)
            .orElseThrow( () -> new NotFoundException("P-500","Usuario no encontrado"));

        // Verificamos si el usuario es miembro del grupo.

        GroupUserId groupUserId = new GroupUserId(groupId, userId);
        GroupUser groupUser = groupUserRepository.findById(groupUserId)
            .orElseThrow(() -> new NotFoundException("P-505","Usuario no encontrado en el grupo"));
 
        groupUser.setLeader(leader);
        groupUserRepository.save(groupUser);
        
        return GroupMemberResponseDTO.builder()
            .groupName(group.getName())
            .userName(user.getName())
            .isLeader(leader)
        .build();
    }

    @Override
    @Transactional
    public GroupEventResponseDTO addEventToGroup(int groupId, int eventId) {
        Group group = repository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("P-502","Grupo no encontrado"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("P-501","Grupo no encontrado"));

        if (group.getEvents().contains(event)) {
            throw new AlreadyExistsException("P-402","El evento ya está asociado al grupo");
        }

        // Como es relacion bidireccional, agregamos tanto el evento al grupo como el grupo al evento
        group.addEvent(event); // Dentro del metodo de group, se agrega el grupo al evento

        // Se guardan el evento y el grupo
        repository.save(group);
        
        return GroupEventResponseDTO.builder()
            .groupName(group.getName())
            .eventName(event.getName())
        .build();
    }

    @Override
    public GroupEventResponseDTO removeEventFromGroup(int groupId, int eventId) {
        Group group = repository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("P-502","Grupo no encontrado"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("P-501","Grupo no encontrado"));

        if (!group.getEvents().contains(event)) {
            throw new NotFoundException("P-506","El evento no está asociado al grupo");
        }

        group.removeEvent(event); // Dentro del metodo de group, se elimina el grupo del evento

        // Se guardan el evento y el grupo
        repository.save(group);

        return GroupEventResponseDTO.builder()
            .groupName(group.getName())
            .eventName(event.getName())
        .build();
    }

    
}