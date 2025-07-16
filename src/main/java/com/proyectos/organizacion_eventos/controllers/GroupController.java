package com.proyectos.organizacion_eventos.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyectos.organizacion_eventos.dto.GroupDTO;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.services.GroupService;
import com.proyectos.organizacion_eventos.services.UserService;
import com.proyectos.organizacion_eventos.utils.AuthUtilForGroup;
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
    private UserService userService;

    @Autowired
    private GroupService service;

    @Autowired
    private AuthUtilForGroup authLeaderAdmin;

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

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> addMember(
            @PathVariable int groupId,
            @PathVariable int userId,
            // Seteamos por defecto que sea false si no viene en la URL el parametro del si es lider
            @RequestParam(defaultValue = "false") boolean isLeader) {

        // Manejamos errores por si no existe uno o otro
        
        try {

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
        } catch (RuntimeException e) {
            // Manejamos los errores que se tiran desde el Service

            if (e.getMessage().contains("Grupo no encontrado") || e.getMessage().contains("Usuario no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            if (e.getMessage().contains("ya es miembro")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }   
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

        Optional<GroupUser> groupUserOpt = service.removeMember(groupId, userId);
        if (groupUserOpt.isPresent()) {
            String userName = groupUserOpt.get().getUser().getName();
            String groupName = groupUserOpt.get().getGroup().getName();
            return ResponseEntity.ok(Map.of(
            "mensaje", "El usuario " + userName + " fue eliminado del grupo " + groupName
            ));
        }
        return ResponseEntity.notFound().build();
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

        Optional<GroupUser> groupUserOpt= service.modifyLeader(groupId, userId, isLeader);
        
        if (groupUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "El usuario no forma parte del grupo"));

        }                
        return ResponseEntity.ok(Map.of(
            "mensaje", "El usuario fue " + (isLeader ? "asignado como lider" : "removido como lider")));
        
         
    }

    @PostMapping("/{groupId}/events/{eventId}")
    public ResponseEntity<?> addEvent(@PathVariable int groupId, @PathVariable int eventId) {
        
        // Validacion si es lider o admin, llamamos a la clase authLeaderAdmin para validar
        ResponseEntity<?> validation = authLeaderAdmin.validationAdminOrLeader(groupId);
        if (validation != null) {
            return validation;
        }

        Optional<String> errorResult = service.addEventToGroup(groupId, eventId);
        if (errorResult.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", errorResult.get()));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Evento agregado al grupo correctamente"));
    }
    
}