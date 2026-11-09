package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Plato;
import com.saborgourmet.erp.services.PlatoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class PlatoController {

    private final PlatoService platoService;

    public PlatoController(PlatoService platoService) {
        this.platoService = platoService;
    }

    // Listar todos los platos
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Plato>> listarTodos() {

        return ResponseEntity.ok(
            platoService.listarTodos()
        );
    }

    // Listar platos disponibles
    @GetMapping("/disponibles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Plato>> listarDisponibles() {

        return ResponseEntity.ok(
            platoService.listarDisponibles()
        );
    }

    // Buscar platos por categoria
    @GetMapping("/categoria")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Plato>> buscarPorCategoria(
            @RequestParam String nombre) {

        return ResponseEntity.ok(
            platoService.buscarPorCategoria(nombre)
        );
    }

    // Buscar plato por ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Plato> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            platoService.buscarPorId(id)
        );
    }

    // Registrar un nuevo plato
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Plato> registrar(
            @RequestBody Plato plato) {

        Plato nuevoPlato = platoService.registrar(plato);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoPlato);
    }

    // Actualizar un plato
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Plato> actualizar(
            @PathVariable Long id,
            @RequestBody Plato plato) {

        return ResponseEntity.ok(
            platoService.actualizar(id, plato)
        );
    }

    // Eliminar un plato
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        platoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
