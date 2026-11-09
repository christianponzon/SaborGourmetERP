package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Insumo;
import com.saborgourmet.erp.services.InsumoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    // ==========================================
    // LISTAR TODOS LOS INSUMOS
    // ==========================================
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Insumo>> listarTodos() {

        return ResponseEntity.ok(
                insumoService.listarTodos()
        );
    }

    // ==========================================
    // LISTAR INSUMOS ACTIVOS
    // ==========================================
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Insumo>> listarActivos() {

        return ResponseEntity.ok(
                insumoService.listarActivos()
        );
    }

    // ==========================================
    // LISTAR INSUMOS CON STOCK BAJO
    // ==========================================
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Insumo>> listarStockBajo() {

        return ResponseEntity.ok(
                insumoService.listarStockBajo()
        );
    }

    // ==========================================
    // LISTAR INSUMOS AGOTADOS
    // ==========================================
    @GetMapping("/agotados")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Insumo>> listarAgotados() {

        return ResponseEntity.ok(
                insumoService.listarAgotados()
        );
    }

    // ==========================================
    // BUSCAR POR CATEGORIA
    // ==========================================
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<List<Insumo>> buscarPorCategoria(
            @PathVariable String categoria) {

        return ResponseEntity.ok(
                insumoService.buscarPorCategoria(categoria)
        );
    }

    // ==========================================
    // CONTAR TODOS LOS INSUMOS
    // ==========================================
    @GetMapping("/total")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Map<String, Long>> contarTodos() {

        return ResponseEntity.ok(
                Map.of("total", insumoService.contarTodos())
        );
    }

    // ==========================================
    // CONTAR INSUMOS CON STOCK BAJO
    // ==========================================
    @GetMapping("/total-stock-bajo")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Map<String, Long>> contarStockBajo() {

        return ResponseEntity.ok(
                Map.of(
                        "totalStockBajo",
                        insumoService.contarStockBajo()
                )
        );
    }

    // ==========================================
    // BUSCAR INSUMO POR ID
    // ==========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<Insumo> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                insumoService.buscarPorId(id)
        );
    }

    // ==========================================
    // REGISTRAR INSUMO
    // ==========================================
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Insumo> registrar(
            @RequestBody Insumo insumo) {

        Insumo nuevoInsumo = insumoService.registrar(insumo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoInsumo);
    }

    // ==========================================
    // ACTUALIZAR INSUMO
    // ==========================================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Insumo> actualizar(
            @PathVariable Long id,
            @RequestBody Insumo insumo) {

        return ResponseEntity.ok(
                insumoService.actualizar(id, insumo)
        );
    }

    // ==========================================
    // REGISTRAR ENTRADA DE STOCK
    // ==========================================
    @PatchMapping("/{id}/entrada")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Insumo> registrarEntrada(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> datos) {

        BigDecimal cantidad = datos.get("cantidad");

        return ResponseEntity.ok(
                insumoService.registrarEntrada(id, cantidad)
        );
    }

    // ==========================================
    // REGISTRAR SALIDA DE STOCK
    // ==========================================
    @PatchMapping("/{id}/salida")
    @PreAuthorize("hasAnyRole('GERENTE', 'COCINERO')")
    public ResponseEntity<Insumo> registrarSalida(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> datos) {

        BigDecimal cantidad = datos.get("cantidad");

        return ResponseEntity.ok(
                insumoService.registrarSalida(id, cantidad)
        );
    }

    // ==========================================
    // ACTIVAR INSUMO
    // ==========================================
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Insumo> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                insumoService.activar(id)
        );
    }

    // ==========================================
    // DESACTIVAR INSUMO
    // ==========================================
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Insumo> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                insumoService.desactivar(id)
        );
    }
}
