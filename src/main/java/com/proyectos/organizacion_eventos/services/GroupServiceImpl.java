package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.repositories.GroupRepository;

@Service
public class GroupServiceImpl implements GroupService {

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
}
