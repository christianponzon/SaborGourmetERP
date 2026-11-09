package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/repartidor")
public class RepartidorController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<Map<String, Object>> dashboard() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
            "mensaje",
            "Bienvenido al dashboard del repartidor"
        );

        respuesta.put("rol", "REPARTIDOR");
        respuesta.put("modulo", "Gestion de delivery");
        respuesta.put("estado", "Activo");

        respuesta.put(
            "funcionalidades",
            new String[]{
                "Consultar pedidos asignados",
                "Ver detalles de entregas",
                "Consultar direccion de entrega",
                "Marcar pedido en camino",
                "Confirmar pedido entregado",
                "Consultar historial de entregas"
            }
        );

        return ResponseEntity.ok(respuesta);
    }
}
