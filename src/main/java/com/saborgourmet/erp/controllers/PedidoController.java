package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.models.Pedido;
import com.saborgourmet.erp.services.PedidoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Listar todos los pedidos
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO', 'COCINERO', 'CAJERO')")
    public ResponseEntity<List<Pedido>> listarTodos() {

        return ResponseEntity.ok(
                pedidoService.listarTodos()
        );
    }

    // Consultar pedidos por estado
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO', 'COCINERO', 'CAJERO')")
    public ResponseEntity<List<Pedido>> buscarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                pedidoService.buscarPorEstado(estado)
        );
    }

    // Consultar pedidos de una mesa
    @GetMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO', 'CAJERO')")
    public ResponseEntity<List<Pedido>> buscarPorMesa(
            @PathVariable Long mesaId) {

        return ResponseEntity.ok(
                pedidoService.buscarPorMesa(mesaId)
        );
    }

    // Consultar pedidos de un mesero
    @GetMapping("/mesero/{meseroId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<List<Pedido>> buscarPorMesero(
            @PathVariable Long meseroId) {

        return ResponseEntity.ok(
                pedidoService.buscarPorMesero(meseroId)
        );
    }

    // Consultar pedido por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO', 'COCINERO', 'CAJERO')")
    public ResponseEntity<Pedido> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pedidoService.buscarPorId(id)
        );
    }

    // Registrar un nuevo pedido
    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Pedido> registrar(
            @RequestBody Pedido pedido) {

        Pedido nuevoPedido = pedidoService.registrar(pedido);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoPedido);
    }

    // Actualizar el estado de un pedido
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO', 'COCINERO')")
    public ResponseEntity<Pedido> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String nuevoEstado = datos.get("estado");

        return ResponseEntity.ok(
                pedidoService.cambiarEstado(id, nuevoEstado)
        );
    }

    // Cancelar un pedido
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<Pedido> cancelar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pedidoService.cancelar(id)
        );
    }
}
