package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Pago;
import com.saborgourmet.erp.services.PagoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    // ==========================================
    // LISTAR TODOS LOS PAGOS
    // ==========================================
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<List<Pago>> listarTodos() {

        return ResponseEntity.ok(
                pagoService.listarTodos()
        );
    }

    // ==========================================
    // CONSULTAR INGRESOS TOTALES
    // ==========================================
    @GetMapping("/ingresos/totales")
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<Map<String, BigDecimal>>
            ingresosTotales() {

        return ResponseEntity.ok(
                Map.of(
                        "ingresosTotales",
                        pagoService.calcularIngresosTotales()
                )
        );
    }

    // ==========================================
    // CONSULTAR INGRESOS DEL DIA
    // ==========================================
    @GetMapping("/ingresos/hoy")
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<Map<String, BigDecimal>>
            ingresosDelDia() {

        return ResponseEntity.ok(
                Map.of(
                        "ingresosHoy",
                        pagoService.calcularIngresosDelDia()
                )
        );
    }

    // ==========================================
    // BUSCAR PAGOS POR PEDIDO
    // ==========================================
    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<List<Pago>> buscarPorPedido(
            @PathVariable Long pedidoId) {

        return ResponseEntity.ok(
                pagoService.buscarPorPedido(pedidoId)
        );
    }

    // ==========================================
    // BUSCAR PAGOS POR CAJERO
    // ==========================================
    @GetMapping("/cajero/{cajeroId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<List<Pago>> buscarPorCajero(
            @PathVariable Long cajeroId) {

        return ResponseEntity.ok(
                pagoService.buscarPorCajero(cajeroId)
        );
    }

    // ==========================================
    // BUSCAR PAGOS POR ESTADO
    // ==========================================
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<List<Pago>> buscarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                pagoService.buscarPorEstado(estado)
        );
    }

    // ==========================================
    // BUSCAR PAGO POR ID
    // ==========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<Pago> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pagoService.buscarPorId(id)
        );
    }

    // ==========================================
    // REGISTRAR UN PAGO
    // ==========================================
    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'CAJERO')")
    public ResponseEntity<Pago> registrar(
            @RequestBody Pago pago) {

        Pago nuevoPago = pagoService.registrar(pago);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoPago);
    }
}
