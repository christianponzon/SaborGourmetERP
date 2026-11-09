package com.saborgourmet.erp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> inicio() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("nombre", "Sabor Gourmet ERP");
        respuesta.put("mensaje", "Bienvenido a nuestra API REST");
        respuesta.put("tecnologia", "Spring Boot");
        respuesta.put("version", "1.0.0");
        respuesta.put("estado", "Activo");

        return respuesta;
    }
}
