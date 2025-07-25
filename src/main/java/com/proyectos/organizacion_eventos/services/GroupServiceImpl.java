package com.proyectos.organizacion_eventos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            repository.delete(group);
            return groupOptional; // Retornamos el grupo eliminado
        }
        // Si no se encuentra el grupo, retornamos un Optional vacío
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
    public Optional<GroupUser> modifyLeader(int groupId, int userId, boolean leader) {

        // Verificamos si el usuario es miembro del grupo.

        GroupUserId groupUserId = new GroupUserId(groupId, userId);
        Optional<GroupUser> groupUser = groupUserRepository.findById(groupUserId);
        groupUser.ifPresent(gr -> {
            gr.setLeader(leader);
            groupUserRepository.save(gr);
        });
        return groupUser;
    }

    @Override
    public Optional<String> addEventToGroup(int groupId, int eventId) {
        Optional<Group> groupOpt = repository.findById(groupId);
        if (groupOpt.isEmpty()) return Optional.of("Evento no encontrado");

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) return Optional.of("Evento no encontrado");

        Group group = groupOpt.get();
        Event event = eventOpt.get();

        if (group.getEvents().contains(event)) {
            return Optional.of("El evento ya está asociado al grupo");
        }

        // Como es relacion bidireccional, agregamos tanto el evento al grupo como el grupo al evento
        group.addEvent(event); // Dentro del metodo de group, se agrega el grupo al evento

        // Se guardan el evento y el grupo
        repository.save(group);
        
        return Optional.empty(); // No hay error, por lo que retornamos un Optional vacío
    }

    
}