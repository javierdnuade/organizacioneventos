package com.proyectos.organizacion_eventos.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.proyectos.organizacion_eventos.dto.ErrorDTO;
import com.proyectos.organizacion_eventos.exceptions.AlreadyExistsException;
import com.proyectos.organizacion_eventos.exceptions.BadRequestException;
import com.proyectos.organizacion_eventos.exceptions.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> hasAlreadyExists(AlreadyExistsException ex) {
        ErrorDTO error = ErrorDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409 Conflict
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDTO> notFound(NotFoundException ex) {
        ErrorDTO error = ErrorDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorDTO> badRequest(BadRequestException ex) {
        ErrorDTO error = ErrorDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400 Bad Request
    }

}
