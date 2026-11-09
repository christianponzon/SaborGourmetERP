package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class PerfilController {

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(
            Authentication authentication) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("mensaje", "Usuario autenticado correctamente");
        respuesta.put("correo", authentication.getName());

        respuesta.put(
            "roles",
            authentication.getAuthorities()
                    .stream()
                    .map(autoridad -> autoridad.getAuthority())
                    .toList()
        );

        return ResponseEntity.ok(respuesta);
    }
}
