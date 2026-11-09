package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Proveedor;
import com.saborgourmet.erp.services.ProveedorService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    // ==========================================
    // LISTAR TODOS LOS PROVEEDORES
    // ==========================================
    @GetMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<Proveedor>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // ==========================================
    // LISTAR PROVEEDORES ACTIVOS
    // ==========================================
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Proveedor>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    // ==========================================
    // LISTAR PROVEEDORES INACTIVOS
    // ==========================================
    @GetMapping("/inactivos")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<Proveedor>> listarInactivos() {
        return ResponseEntity.ok(service.listarInactivos());
    }

    // ==========================================
    // BUSCAR PROVEEDORES POR RAZON SOCIAL
    // ==========================================
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<Proveedor>> buscarPorNombre(
            @RequestParam String nombre) {

        return ResponseEntity.ok(
                service.buscarPorRazonSocial(nombre)
        );
    }

    // ==========================================
    // BUSCAR PROVEEDORES POR CATEGORIA
    // ==========================================
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Proveedor>> buscarPorCategoria(
            @PathVariable String categoria) {

        return ResponseEntity.ok(
                service.buscarPorCategoria(categoria)
        );
    }

    // ==========================================
    // BUSCAR PROVEEDOR POR RUC
    // ==========================================
    @GetMapping("/ruc/{ruc}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Proveedor> buscarPorRuc(
            @PathVariable String ruc) {

        return ResponseEntity.ok(service.buscarPorRuc(ruc));
    }

    // ==========================================
    // INDICADORES DE PROVEEDORES
    // ==========================================
    @GetMapping("/total")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Long> contarTodos() {
        return ResponseEntity.ok(service.contarTodos());
    }

    @GetMapping("/total-activos")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Long> contarActivos() {
        return ResponseEntity.ok(service.contarActivos());
    }

    @GetMapping("/total-inactivos")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Long> contarInactivos() {
        return ResponseEntity.ok(service.contarInactivos());
    }

    // ==========================================
    // BUSCAR PROVEEDOR POR ID
    // ==========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Proveedor> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ==========================================
    // REGISTRAR PROVEEDOR
    // ==========================================
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Proveedor> registrar(
            @RequestBody Proveedor proveedor) {

        Proveedor nuevo = service.registrar(proveedor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevo);
    }

    // ==========================================
    // ACTUALIZAR PROVEEDOR
    // ==========================================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Proveedor> actualizar(
            @PathVariable Long id,
            @RequestBody Proveedor proveedor) {

        return ResponseEntity.ok(
                service.actualizar(id, proveedor)
        );
    }

    // ==========================================
    // ACTIVAR PROVEEDOR
    // ==========================================
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Proveedor> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.activar(id));
    }

    // ==========================================
    // DESACTIVAR PROVEEDOR
    // ==========================================
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Proveedor> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.desactivar(id));
    }

    // ==========================================
    // ELIMINAR PROVEEDOR
    // ==========================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
