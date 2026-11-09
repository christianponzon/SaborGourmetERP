package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Reserva;
import com.saborgourmet.erp.services.ReservaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // ==========================================
    // LISTAR TODAS LAS RESERVAS
    // ==========================================
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Reserva>> listarTodas() {

        return ResponseEntity.ok(
                reservaService.listarTodas()
        );
    }

    // ==========================================
    // BUSCAR RESERVAS POR FECHA
    // ==========================================
    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Reserva>> buscarPorFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {

        return ResponseEntity.ok(
                reservaService.buscarPorFecha(fecha)
        );
    }

    // ==========================================
    // BUSCAR RESERVAS POR MESA
    // ==========================================
    @GetMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Reserva>> buscarPorMesa(
            @PathVariable Long mesaId) {

        return ResponseEntity.ok(
                reservaService.buscarPorMesa(mesaId)
        );
    }

    // ==========================================
    // BUSCAR RESERVAS POR ESTADO
    // ==========================================
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Reserva>> buscarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                reservaService.buscarPorEstado(estado)
        );
    }

    // ==========================================
    // BUSCAR RESERVA POR ID
    // ==========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Reserva> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservaService.buscarPorId(id)
        );
    }

    // ==========================================
    // REGISTRAR UNA RESERVA
    // ==========================================
    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Reserva> registrar(
            @RequestBody Reserva reserva) {

        Reserva nuevaReserva =
                reservaService.registrar(reserva);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaReserva);
    }

    // ==========================================
    // ACTUALIZAR UNA RESERVA
    // ==========================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Reserva> actualizar(
            @PathVariable Long id,
            @RequestBody Reserva reserva) {

        return ResponseEntity.ok(
                reservaService.actualizar(id, reserva)
        );
    }

    // ==========================================
    // CAMBIAR ESTADO DE RESERVA
    // ==========================================
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Reserva> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String estado = datos.get("estado");

        return ResponseEntity.ok(
                reservaService.cambiarEstado(id, estado)
        );
    }

    // ==========================================
    // CANCELAR UNA RESERVA
    // ==========================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Reserva> cancelar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservaService.cancelar(id)
        );
    }
}
