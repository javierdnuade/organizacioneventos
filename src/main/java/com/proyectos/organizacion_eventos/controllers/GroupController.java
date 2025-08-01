package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.dto.GroupEventResponseDTO;
import com.proyectos.organizacion_eventos.dto.GroupMemberResponseDTO;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.services.GroupService;
import com.proyectos.organizacion_eventos.services.UserService;
import com.proyectos.organizacion_eventos.utils.AuthUtilForGroup;
import com.proyectos.organizacion_eventos.utils.ControllerUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final UserService userService;

    private final GroupService service;

    private final AuthUtilForGroup authLeaderAdmin;

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

        Group created = service.save(group);
        // Convertimso el grupo en DTO para su impresion
        GroupDTO dto = GroupDTO.builder()
            .id(created.getId())
            .name(created.getName())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable int id) {
        GroupDTO deletedGroup = service.delete(id);
        return ResponseEntity.ok(deletedGroup);
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> addMember(
            @PathVariable int groupId,
            @PathVariable int userId,
            // Seteamos por defecto que sea false si no viene en la URL el parametro del si es lider
            @RequestParam(defaultValue = "false") boolean isLeader) {

        // Manejamos errores por si no existe uno o otro
        

            // Validacion si es lider o admin, llamamos a la clase authLeaderAdmin para validar
            ResponseEntity<?> validation = authLeaderAdmin.validationAdminOrLeader(groupId);
            if (validation != null) {
                return validation;
            }

            service.addMember(groupId, userId, isLeader);

            String groupName = service.findById(groupId).map(Group::getName).orElse("desconocido");
            String userName = userService.findById(userId).map(User::getName).orElse("desconocido");

            return ResponseEntity.ok(Map.of(
                "usuario", userName,
                "grupo", groupName
            ));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> deleteMember (
            @PathVariable int groupId,
            @PathVariable int userId) {

        // Validacion si es lider o admin, llamamos a la clase authLeaderAdmin para validar
        ResponseEntity<?> validation = authLeaderAdmin.validationAdminOrLeader(groupId);
        if (validation != null) {
            return validation;
        }

        GroupMemberResponseDTO response = service.removeMember(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/members/{userId}/setLeader")
    public ResponseEntity<?> setLeader(
            @PathVariable int groupId,
            @PathVariable int userId,
            @RequestParam boolean isLeader) {

        // Validacion si es lider o admin, llamamos a la clase authLeaderAdmin para validar
        ResponseEntity<?> validation = authLeaderAdmin.validationAdminOrLeader(groupId);
        if (validation != null) {
            return validation;
        }

        GroupMemberResponseDTO response = service.modifyLeader(groupId, userId, isLeader);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/events/{eventId}")
    public ResponseEntity<?> addEvent(@PathVariable int groupId, @PathVariable int eventId) {
        
        // Validacion si es lider o admin, llamamos a la clase authLeaderAdmin para validar
        ResponseEntity<?> validation = authLeaderAdmin.validationAdminOrLeader(groupId);
        if (validation != null) {
            return validation;
        }

        GroupEventResponseDTO response = service.addEventToGroup(groupId, eventId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/events/{eventId}")
    public ResponseEntity<?> removeEvent(@PathVariable int groupId, @PathVariable int eventId) {
        // Validacion si es lider o admin, llamamos a la clase authLeaderAdmin para validar
        ResponseEntity<?> validation = authLeaderAdmin.validationAdminOrLeader(groupId);
        if (validation != null) {
            return validation;
        }

        GroupEventResponseDTO response = service.removeEventFromGroup(groupId, eventId);
        return ResponseEntity.ok(response);
    }
}