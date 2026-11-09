package com.saborgourmet.erp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey claveSecreta;

    private static final long DURACION_TOKEN = 1000L * 60 * 60 * 8;

    public JwtService(
            @Value("${app.jwt.secret}") String secreto) {

        if (secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                "La clave JWT debe tener al menos 32 bytes"
            );
        }

        this.claveSecreta = Keys.hmacShaKeyFor(
            secreto.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Generar token de autenticacion
    public String generarToken(String correo, String rol) {

        Date ahora = new Date();

        Date expiracion = new Date(
            ahora.getTime() + DURACION_TOKEN
        );

        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(claveSecreta)
                .compact();
    }

    // Extraer datos del token
    public Claims obtenerClaims(String token) {

        return Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Obtener correo del usuario
    public String obtenerCorreo(String token) {

        return obtenerClaims(token).getSubject();
    }

    // Obtener rol del usuario
    public String obtenerRol(String token) {

        return obtenerClaims(token)
                .get("rol", String.class);
    }

    // Verificar firma y fecha de expiracion
    public boolean validarToken(String token) {

        try {
            obtenerClaims(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
