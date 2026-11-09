package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cocinero")
public class CocineroController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('COCINERO')")
    public ResponseEntity<Map<String, Object>> dashboard() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
            "mensaje",
            "Bienvenido al dashboard de cocina"
        );

        respuesta.put("rol", "COCINERO");
        respuesta.put("modulo", "Gestion de cocina");
        respuesta.put("estado", "Activo");

        respuesta.put(
            "funcionalidades",
            new String[]{
                "Consultar pedidos pendientes",
                "Ver detalles de pedidos",
                "Iniciar preparacion",
                "Marcar pedidos como listos",
                "Consultar historial de pedidos"
            }
        );

        return ResponseEntity.ok(respuesta);
    }
}
