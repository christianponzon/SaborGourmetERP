package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mesero")
public class MeseroController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('MESERO')")
    public ResponseEntity<Map<String, Object>> dashboard() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
            "mensaje",
            "Bienvenido al dashboard del mesero"
        );

        respuesta.put("rol", "MESERO");
        respuesta.put("modulo", "Atencion de mesas");
        respuesta.put("estado", "Activo");

        respuesta.put(
            "funcionalidades",
            new String[]{
                "Consultar mesas",
                "Registrar pedidos",
                "Consultar pedidos",
                "Gestionar reservas"
            }
        );

        return ResponseEntity.ok(respuesta);
    }
}
