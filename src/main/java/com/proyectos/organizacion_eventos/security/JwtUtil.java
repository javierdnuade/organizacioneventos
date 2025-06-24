package com.proyectos.organizacion_eventos.security;


import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;



@Component
public class JwtUtil {

    private final SecretKey Secret_Key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String createToken(String username) {
        String token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Desde el momento actual, hasta 1 dia
            .signWith(Secret_Key)
            .compact();

        return token;
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Secret_Key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Secret_Key)
                .build()
                .parseClaimsJws(token);
                return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
