
package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Pago;
import com.saborgourmet.erp.models.Pedido;
import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.repositories.PagoRepository;
import com.saborgourmet.erp.repositories.PedidoRepository;
import com.saborgourmet.erp.repositories.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoService(
            PagoRepository pagoRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {

        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ==========================================
    // LISTAR TODOS LOS PAGOS
    // ==========================================
    public List<Pago> listarTodos() {
        return pagoRepository.listarTodos();
    }

    // ==========================================
    // BUSCAR PAGO POR ID
    // ==========================================
    public Pago buscarPorId(Long id) {

        return pagoRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pago no encontrado"
                        )
                );
    }

    // ==========================================
    // BUSCAR PAGOS POR PEDIDO
    // ==========================================
    public List<Pago> buscarPorPedido(Long pedidoId) {

        if (pedidoRepository.buscarPorId(pedidoId).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pedido no encontrado"
            );
        }

        return pagoRepository.buscarPorPedido(pedidoId);
    }

    // ==========================================
    // BUSCAR PAGOS POR CAJERO
    // ==========================================
    public List<Pago> buscarPorCajero(Long cajeroId) {

        usuarioRepository.buscarPorId(cajeroId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );

        return pagoRepository.buscarPorCajero(cajeroId);
    }

    // ==========================================
    // BUSCAR PAGOS POR ESTADO
    // ==========================================
    public List<Pago> buscarPorEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        return pagoRepository.buscarPorEstado(
                estado.trim().toUpperCase(Locale.ROOT)
        );
    }

    // ==========================================
    // OBTENER USUARIO AUTENTICADO
    // ==========================================
    private Usuario obtenerUsuarioAutenticado() {

        Authentication autenticacion =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || autenticacion.getName() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        String correo = autenticacion.getName();

        Usuario usuario = usuarioRepository
                .buscarPorCorreo(correo)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuario autenticado no encontrado"
                        )
                );

        if (!usuario.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario está desactivado"
            );
        }

        return usuario;
    }

    // ==========================================
    // REGISTRAR UN PAGO
    // ==========================================
    public synchronized Pago registrar(Pago datos) {

        // Identificar al usuario mediante JWT
        Usuario usuario = obtenerUsuarioAutenticado();

        // Solo GERENTE y CAJERO pueden registrar pagos
        if (!"GERENTE".equalsIgnoreCase(usuario.getRol())
                && !"CAJERO".equalsIgnoreCase(usuario.getRol())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para registrar pagos"
            );
        }

        if (datos == null || datos.getPedidoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del pedido es obligatorio"
            );
        }

        // Verificar que el pedido exista
        Pedido pedido = pedidoRepository
                .buscarPorId(datos.getPedidoId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pedido no encontrado"
                        )
                );

        // Solo cobrar pedidos entregados
        if (!"ENTREGADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden cobrar pedidos entregados"
            );
        }

        // Evitar pagos duplicados
        boolean yaPagado = pagoRepository
                .buscarPorPedido(pedido.getId())
                .stream()
                .anyMatch(pago ->
                        "PAGADO".equalsIgnoreCase(pago.getEstado())
                );

        if (yaPagado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este pedido ya tiene un pago registrado"
            );
        }

        // Validar método de pago
        if (datos.getMetodoPago() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El método de pago es obligatorio"
            );
        }

        String metodo = datos.getMetodoPago()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!metodo.equals("EFECTIVO")
                && !metodo.equals("TARJETA")
                && !metodo.equals("YAPE")
                && !metodo.equals("PLIN")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Método de pago no válido"
            );
        }

        // El monto se obtiene del pedido
        BigDecimal monto = pedido.getTotal();

        if (monto == null
                || monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido no tiene un monto válido"
            );
        }

        // Crear pago
        Pago pago = new Pago();

        pago.setPedidoId(pedido.getId());

        // El ID se obtiene del usuario autenticado,
        // nunca del JSON enviado por el cliente.
        pago.setCajeroId(usuario.getId());

        pago.setMonto(monto);
        pago.setMetodoPago(metodo);
        pago.setEstado("PAGADO");

        String referencia = datos.getReferencia();

        pago.setReferencia(
                referencia == null || referencia.isBlank()
                        ? null
                        : referencia.trim()
        );

        pago.setFecha(LocalDateTime.now());

        return pagoRepository.guardar(pago);
    }

    // ==========================================
    // CALCULAR INGRESOS TOTALES
    // ==========================================
    public BigDecimal calcularIngresosTotales() {

        return pagoRepository.listarTodos()
                .stream()
                .filter(pago ->
                        "PAGADO".equalsIgnoreCase(pago.getEstado())
                )
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==========================================
    // CALCULAR INGRESOS DEL DIA
    // ==========================================
    public BigDecimal calcularIngresosDelDia() {

        LocalDate hoy = LocalDate.now();

        return pagoRepository.listarTodos()
                .stream()
                .filter(pago ->
                        "PAGADO".equalsIgnoreCase(pago.getEstado())
                )
                .filter(pago ->
                        pago.getFecha() != null
                        && pago.getFecha().toLocalDate().equals(hoy)
                )
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
