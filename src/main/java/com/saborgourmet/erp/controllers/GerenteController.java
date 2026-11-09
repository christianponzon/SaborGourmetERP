package com.saborgourmet.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gerente")
public class GerenteController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Map<String, Object>> dashboard() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("mensaje", "Bienvenido al dashboard del gerente");
        respuesta.put("rol", "GERENTE");
        respuesta.put("modulo", "Administracion");
        respuesta.put("estado", "Activo");

        return ResponseEntity.ok(respuesta);
    }
}
