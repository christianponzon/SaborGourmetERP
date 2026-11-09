package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @GetMapping("/inicio")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> inicio() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
            "mensaje",
            "Bienvenido a Sabor Gourmet"
        );

        respuesta.put("rol", "CLIENTE");
        respuesta.put("modulo", "Portal del cliente");
        respuesta.put("estado", "Activo");

        respuesta.put(
            "funcionalidades",
            new String[]{
                "Consultar menu",
                "Ver detalles de platos",
                "Realizar pedidos",
                "Consultar mis pedidos",
                "Realizar reservas",
                "Consultar mis reservas",
                "Consultar historial de compras"
            }
        );

        return ResponseEntity.ok(respuesta);
    }
}
