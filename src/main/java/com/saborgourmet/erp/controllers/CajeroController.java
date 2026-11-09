package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cajero")
public class CajeroController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CAJERO')")
    public ResponseEntity<Map<String, Object>> dashboard() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
            "mensaje",
            "Bienvenido al dashboard del cajero"
        );

        respuesta.put("rol", "CAJERO");
        respuesta.put("modulo", "Gestion de caja");
        respuesta.put("estado", "Activo");

        respuesta.put(
            "funcionalidades",
            new String[]{
                "Consultar pagos pendientes",
                "Registrar pagos",
                "Consultar ventas del dia",
                "Gestionar apertura y cierre de caja",
                "Consultar historial de pagos",
                "Generar reportes de caja"
            }
        );

        return ResponseEntity.ok(respuesta);
    }
}
