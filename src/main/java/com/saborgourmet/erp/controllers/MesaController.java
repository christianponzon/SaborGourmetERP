package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Mesa;
import com.saborgourmet.erp.services.MesaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    // Listar todas las mesas
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Mesa>> listarTodas() {

        return ResponseEntity.ok(
            mesaService.listarTodas()
        );
    }

    // Consultar mesas disponibles
    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Mesa>> listarDisponibles() {

        return ResponseEntity.ok(
            mesaService.listarDisponibles()
        );
    }

    // Buscar mesas por estado
    @GetMapping("/estado")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Mesa>> buscarPorEstado(
            @RequestParam String nombre) {

        return ResponseEntity.ok(
            mesaService.buscarPorEstado(nombre)
        );
    }

    // Consultar una mesa por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Mesa> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            mesaService.buscarPorId(id)
        );
    }

    // Registrar una nueva mesa
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Mesa> registrar(
            @RequestBody Mesa mesa) {

        Mesa nuevaMesa = mesaService.registrar(mesa);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaMesa);
    }

    // Actualizar una mesa
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Mesa> actualizar(
            @PathVariable Long id,
            @RequestBody Mesa mesa) {

        return ResponseEntity.ok(
            mesaService.actualizar(id, mesa)
        );
    }

    // Cambiar el estado de una mesa
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Mesa> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String estado = datos.get("estado");

        return ResponseEntity.ok(
            mesaService.cambiarEstado(id, estado)
        );
    }

    // Eliminar una mesa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        mesaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
