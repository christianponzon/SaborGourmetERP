package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Pedido;
import com.saborgourmet.erp.models.DetallePedido;
import com.saborgourmet.erp.models.Plato;
import com.saborgourmet.erp.models.Mesa;
import com.saborgourmet.erp.models.Usuario;

import com.saborgourmet.erp.repositories.PedidoRepository;
import com.saborgourmet.erp.repositories.PlatoRepository;
import com.saborgourmet.erp.repositories.MesaRepository;
import com.saborgourmet.erp.repositories.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PlatoRepository platoRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            PlatoRepository platoRepository,
            MesaRepository mesaRepository,
            UsuarioRepository usuarioRepository) {

        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Listar todos los pedidos
    public List<Pedido> listarTodos() {
        return pedidoRepository.listarTodos();
    }

    // Buscar pedido por ID
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                ));
    }

    // Buscar pedidos por estado
    public List<Pedido> buscarPorEstado(String estado) {

        String estadoValidado = normalizarEstado(estado);

        return pedidoRepository.buscarPorEstado(estadoValidado);
    }

    // Buscar pedidos de una mesa
    public List<Pedido> buscarPorMesa(Long mesaId) {

        if (mesaId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID de la mesa es obligatorio"
            );
        }

        mesaRepository.buscarPorId(mesaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mesa no encontrada"
                ));

        return pedidoRepository.buscarPorMesa(mesaId);
    }

    // Buscar pedidos de un mesero
    public List<Pedido> buscarPorMesero(Long meseroId) {

        if (meseroId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del mesero es obligatorio"
            );
        }

        return pedidoRepository.buscarPorMesero(meseroId);
    }

    // Registrar un nuevo pedido
    public synchronized Pedido registrar(Pedido pedido) {

        if (pedido == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del pedido son obligatorios"
            );
        }

        String tipo = normalizarTipo(pedido.getTipo());

        pedido.setTipo(tipo);

        // Validar mesa para pedidos de salon
        if ("SALON".equals(tipo)) {

            if (pedido.getMesaId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Debe seleccionar una mesa"
                );
            }

            Mesa mesa = mesaRepository
                    .buscarPorId(pedido.getMesaId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Mesa no encontrada"
                    ));

            if (!mesa.isActiva()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La mesa no esta activa"
                );
            }

            if (!"DISPONIBLE".equalsIgnoreCase(
                    mesa.getEstado())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La mesa no esta disponible"
                );
            }

        } else {
            // Delivery y recojo no utilizan mesa
            pedido.setMesaId(null);
        }

        // Validar mesero si fue indicado
        if (pedido.getMeseroId() != null) {

            Usuario usuario = usuarioRepository
                    .buscarPorId(pedido.getMeseroId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Mesero no encontrado"
                    ));

            if (!"MESERO".equalsIgnoreCase(usuario.getRol())
                    || !usuario.isActivo()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El usuario no es un mesero activo"
                );
            }
        }

        // Validar detalles del pedido
        if (pedido.getDetalles() == null ||
                pedido.getDetalles().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe agregar al menos un plato"
            );
        }

        List<DetallePedido> detallesValidados =
                new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        long contadorDetalle = 1;

        for (DetallePedido detalle : pedido.getDetalles()) {

            if (detalle == null ||
                    detalle.getPlatoId() == null ||
                    detalle.getCantidad() == null ||
                    detalle.getCantidad() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cada detalle debe tener un plato y cantidad valida"
                );
            }

            Plato plato = platoRepository
                    .buscarPorId(detalle.getPlatoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Plato no encontrado: " + detalle.getPlatoId()
                    ));

            if (!plato.isDisponible()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El plato no esta disponible: " + plato.getNombre()
                );
            }

            BigDecimal precio = plato.getPrecio();

            if (precio == null ||
                    precio.compareTo(BigDecimal.ZERO) <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El plato no tiene un precio valido"
                );
            }

            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(detalle.getCantidad())
            );

            DetallePedido nuevoDetalle = new DetallePedido();

            nuevoDetalle.setId(contadorDetalle++);
            nuevoDetalle.setPlatoId(plato.getId());
            nuevoDetalle.setNombrePlato(plato.getNombre());
            nuevoDetalle.setCantidad(detalle.getCantidad());
            nuevoDetalle.setPrecioUnitario(precio);
            nuevoDetalle.setSubtotal(subtotal);
            nuevoDetalle.setObservaciones(
                    detalle.getObservaciones()
            );

            detallesValidados.add(nuevoDetalle);

            total = total.add(subtotal);
        }

        // Asignar datos controlados por el servidor
        pedido.setId(null);
        pedido.setEstado("PENDIENTE");
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setDetalles(detallesValidados);

        Pedido guardado = pedidoRepository.guardar(pedido);

        // Ocupar mesa cuando se registra pedido de salon
        if ("SALON".equals(tipo)) {

            Mesa mesa = mesaRepository
                    .buscarPorId(pedido.getMesaId())
                    .orElseThrow();

            mesa.setEstado("OCUPADA");
            mesaRepository.guardar(mesa);
        }

        return guardado;
    }

    // Cambiar estado de un pedido
    public synchronized Pedido cambiarEstado(
            Long id,
            String nuevoEstado) {

        Pedido pedido = buscarPorId(id);

        String estado = normalizarEstado(nuevoEstado);

        String actual = pedido.getEstado();

        boolean permitido = switch (actual) {
            case "PENDIENTE" ->
                    Set.of("EN_PREPARACION", "CANCELADO")
                            .contains(estado);

            case "EN_PREPARACION" ->
                    Set.of("LISTO", "CANCELADO")
                            .contains(estado);

            case "LISTO" ->
                    Set.of("ENTREGADO", "CANCELADO")
                            .contains(estado);

            default -> false;
        };

        if (!permitido) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cambio de estado no permitido: "
                            + actual + " a " + estado
            );
        }

        pedido.setEstado(estado);

        return pedidoRepository.guardar(pedido);
    }

    // Cancelar un pedido sin eliminar su historial
    public synchronized Pedido cancelar(Long id) {

        return cambiarEstado(id, "CANCELADO");
    }

    // Validar tipo de pedido
    private String normalizarTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de pedido es obligatorio"
            );
        }

        String valor = tipo.trim()
                .toUpperCase(Locale.ROOT);

        if (!Set.of("SALON", "DELIVERY", "RECOJO")
                .contains(valor)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo invalido. Use SALON, DELIVERY o RECOJO"
            );
        }

        return valor;
    }

    // Validar estado del pedido
    private String normalizarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        String valor = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!Set.of(
                "PENDIENTE",
                "EN_PREPARACION",
                "LISTO",
                "ENTREGADO",
                "CANCELADO"
        ).contains(valor)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado de pedido invalido"
            );
        }

        return valor;
    }
}
