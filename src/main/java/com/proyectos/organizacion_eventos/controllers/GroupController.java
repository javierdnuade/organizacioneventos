package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.services.GroupService;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService service;

    @GetMapping
    public ResponseEntity<List<GroupDTO>> list() {
        List<GroupDTO> groups = service.findAll();
        if (groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> showGroup (@PathVariable int id) {
        return service.getGroupDTO(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    

    @PostMapping
    public ResponseEntity<?> create (@Valid @RequestBody Group group, BindingResult result) {
        ResponseEntity<?> errors = ControllerUtils.getErrorsResponse(result);
        if (errors != null) {
            return errors;
        }

        try {
            Group created = service.save(group);
            // Convertimso el grupo en DTO para su impresion
            GroupDTO dto = GroupDTO.builder()
                .id(created.getId())
                .name(created.getName())
            .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable int id) {
        Optional<Group> groupOptional = service.findById(id);
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            GroupDTO dto = GroupDTO.builder()   
                .id(group.getId())
                .name(group.getName())
            .build();
            service.delete(id);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

}
