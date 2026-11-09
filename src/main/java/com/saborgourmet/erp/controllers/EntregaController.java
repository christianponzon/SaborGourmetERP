package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Entrega;
import com.saborgourmet.erp.services.EntregaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    // ==========================================
    // LISTAR TODAS LAS ENTREGAS
    // ==========================================
    @GetMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<Entrega>> listarTodas() {

        return ResponseEntity.ok(
                entregaService.listarTodas()
        );
    }

    // ==========================================
    // BUSCAR ENTREGAS POR ESTADO
    // ==========================================
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<Entrega>> buscarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                entregaService.buscarPorEstado(estado)
        );
    }

    // ==========================================
    // BUSCAR ENTREGAS POR REPARTIDOR
    // ==========================================
    @GetMapping("/repartidor/{repartidorId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'REPARTIDOR')")
    public ResponseEntity<List<Entrega>> buscarPorRepartidor(
            @PathVariable Long repartidorId) {

        return ResponseEntity.ok(
                entregaService.buscarPorRepartidor(repartidorId)
        );
    }

    // ==========================================
    // BUSCAR ENTREGA POR PEDIDO
    // ==========================================
    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'REPARTIDOR')")
    public ResponseEntity<Entrega> buscarPorPedido(
            @PathVariable Long pedidoId) {

        return ResponseEntity.ok(
                entregaService.buscarPorPedido(pedidoId)
        );
    }

    // ==========================================
    // BUSCAR ENTREGA POR ID
    // ==========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'REPARTIDOR')")
    public ResponseEntity<Entrega> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                entregaService.buscarPorId(id)
        );
    }

    // ==========================================
    // REGISTRAR ENTREGA
    // ==========================================
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Entrega> registrar(
            @RequestBody Entrega entrega) {

        Entrega nuevaEntrega =
                entregaService.registrar(entrega);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaEntrega);
    }

    // ==========================================
    // ASIGNAR REPARTIDOR
    // ==========================================
    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Entrega> asignarRepartidor(
            @PathVariable Long id,
            @RequestBody Map<String, Long> datos) {

        Long repartidorId = datos.get("repartidorId");

        return ResponseEntity.ok(
                entregaService.asignarRepartidor(
                        id,
                        repartidorId
                )
        );
    }

    // ==========================================
    // CAMBIAR ESTADO DE ENTREGA
    // ==========================================
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('GERENTE', 'REPARTIDOR')")
    public ResponseEntity<Entrega> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String estado = datos.get("estado");

        return ResponseEntity.ok(
                entregaService.cambiarEstado(id, estado)
        );
    }

    // ==========================================
    // CANCELAR ENTREGA
    // ==========================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Entrega> cancelar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                entregaService.cancelar(id)
        );
    }
}
