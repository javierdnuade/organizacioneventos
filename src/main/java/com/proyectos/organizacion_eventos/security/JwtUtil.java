package com.proyectos.organizacion_eventos.security;


import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import javax.crypto.SecretKey;



@Component
public class JwtUtil {

    private final SecretKey Secret_Key = Jwts.SIG.HS256.key().build();
    

    public String createToken(String username) {
        String token = Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86400000)) // Desde el momento actual, hasta 1 dia
            .signWith(Secret_Key)
            .compact();

        return token;
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(Secret_Key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(Secret_Key)
                .build()
                .parseSignedClaims(token);
                return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
