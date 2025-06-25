package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.GroupUserId;
import com.proyectos.organizacion_eventos.repositories.GroupRepository;
import com.proyectos.organizacion_eventos.repositories.GroupUserRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository repository;

    @Override
    public List<GroupDTO> findAll() {
        List<Group> groups = (List<Group>) repository.findAll();
        return groups.stream().map(group -> GroupDTO.builder() // Creamos el GroupDTO por cada grupo existente
            .id(group.getId())
            .name(group.getName())
            .members(   // Creamos la lsita de miembros mediante la clase estatica dentro de GroupDTO
                group.getMembers().stream().map(mmb -> GroupDTO.MemberDTO.builder()
                    .name(mmb.getUser().getName())
                    .isLeader(mmb.isLeader())
                    .build()) // Build del miembro dentro del grupo
                .collect(Collectors.toList())) // Convertimos el stream de miembros a una lista para pasarla a la entidad DTO
            .build()) // CConstruimos el GroupDTO completo
        .collect(Collectors.toList()); // Reunimos todos los GroupDTO del stream en una lista
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
    public Optional<GroupDTO> getGroupDTO(int id) {
        return repository.findById(id)
            .map(group -> GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .build());
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

    
}