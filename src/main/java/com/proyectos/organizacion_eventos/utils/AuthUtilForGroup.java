package com.proyectos.organizacion_eventos.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.proyectos.organizacion_eventos.services.GroupService;

@Component
public class AuthUtilForGroup {

    @Autowired
    private GroupService groupService;

    public ResponseEntity<?> validationAdminOrLeader(int grupoId) {
        // Revisamos rol del que maneja la solicitud
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Verificamos is es ADMIN del sistema
        boolean admin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Verificamos si es lider lel grupo
        boolean leader = groupService.isLeader(grupoId, currentUsername);

        if (!admin && !leader) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Solo un lider o admin pueden borrar miembros de un grupo"));
        }    
        return null;
    }
}
