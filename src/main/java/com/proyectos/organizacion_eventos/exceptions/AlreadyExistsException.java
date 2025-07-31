package com.proyectos.organizacion_eventos.exceptions;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException{
    
    private final String code;

    public AlreadyExistsException(String code, String message) {
        super(message);
        this.code = code;
    }
}
