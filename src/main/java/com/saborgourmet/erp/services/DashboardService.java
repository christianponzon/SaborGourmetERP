package com.saborgourmet.erp.services;

import com.saborgourmet.erp.dto.DashboardGerenteDTO;
import com.saborgourmet.erp.models.Pedido;
import com.saborgourmet.erp.repositories.PedidoRepository;
import com.saborgourmet.erp.repositories.MesaRepository;
import com.saborgourmet.erp.repositories.PlatoRepository;
import com.saborgourmet.erp.repositories.InsumoRepository;
import com.saborgourmet.erp.repositories.ProveedorRepository;
import com.saborgourmet.erp.dto.DashboardMeseroDTO;
import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.repositories.UsuarioRepository;
import com.saborgourmet.erp.dto.DashboardCajeroDTO;
import com.saborgourmet.erp.models.Pago;
import com.saborgourmet.erp.repositories.PagoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    
    private final PedidoRepository pedidoRepository;
    private final PagoService pagoService;
    private final MesaRepository mesaRepository;
    private final PlatoRepository platoRepository;
    private final InsumoRepository insumoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoRepository pagoRepository;

    public DashboardService(
    PedidoRepository pedidoRepository,
    PagoService pagoService,
    MesaRepository mesaRepository,
    PlatoRepository platoRepository,
    InsumoRepository insumoRepository,
    ProveedorRepository proveedorRepository,
    UsuarioRepository usuarioRepository,
    PagoRepository pagoRepository) {

    this.pedidoRepository = pedidoRepository;
    this.pagoService = pagoService;
    this.mesaRepository = mesaRepository;
    this.platoRepository = platoRepository;
    this.insumoRepository = insumoRepository;
    this.proveedorRepository = proveedorRepository;
    this.usuarioRepository = usuarioRepository;
    this.pagoRepository = pagoRepository;
}

    // ==========================================
    // DASHBOARD DEL GERENTE
    // ==========================================
    public DashboardGerenteDTO obtenerDashboardGerente() {

        DashboardGerenteDTO dashboard =
                new DashboardGerenteDTO();

        LocalDate hoy = LocalDate.now();

        dashboard.setFecha(hoy);

        // ======================================
        // ESTADISTICAS DE PEDIDOS
        // ======================================

        List<Pedido> pedidos =
                pedidoRepository.listarTodos();

        dashboard.setTotalPedidos(pedidos.size());

        long pedidosDelDia = pedidos.stream()
                .filter(pedido ->
                        pedido.getFecha() != null
                        && pedido.getFecha()
                                .toLocalDate()
                                .equals(hoy))
                .count();

        dashboard.setPedidosDelDia(pedidosDelDia);

        dashboard.setPedidosPendientes(
                contarPedidosPorEstado(pedidos, "PENDIENTE")
        );

        dashboard.setPedidosEnPreparacion(
                contarPedidosPorEstado(pedidos, "EN_PREPARACION")
        );

        dashboard.setPedidosEntregados(
                contarPedidosPorEstado(pedidos, "ENTREGADO")
        );

        dashboard.setPedidosCancelados(
                contarPedidosPorEstado(pedidos, "CANCELADO")
        );

        // ======================================
        // ESTADISTICAS DE INGRESOS
        // ======================================

        dashboard.setIngresosTotales(
                pagoService.calcularIngresosTotales()
        );

        dashboard.setIngresosDelDia(
                pagoService.calcularIngresosDelDia()
        );

        // ======================================
        // ESTADISTICAS DE MESAS
        // ======================================

        dashboard.setTotalMesas(
                mesaRepository.listarTodas().size()
        );

        dashboard.setMesasDisponibles(
                mesaRepository.listarDisponibles().size()
        );

        dashboard.setMesasOcupadas(
                mesaRepository.buscarPorEstado("OCUPADA")
                        .stream()
                        .filter(mesa -> mesa.isActiva())
                        .count()
        );

        dashboard.setMesasReservadas(
                mesaRepository.buscarPorEstado("RESERVADA")
                        .stream()
                        .filter(mesa -> mesa.isActiva())
                        .count()
        );

        // ======================================
        // ESTADISTICAS DE PLATOS
        // ======================================

        dashboard.setTotalPlatos(
                platoRepository.listarTodos().size()
        );

        dashboard.setPlatosDisponibles(
                platoRepository.listarDisponibles().size()
        );

        // ======================================
        // ESTADISTICAS DE INVENTARIO
        // ======================================

        dashboard.setTotalInsumos(
                insumoRepository.contarTodos()
        );

        dashboard.setInsumosActivos(
                insumoRepository.listarActivos().size()
        );

        dashboard.setInsumosStockBajo(
                insumoRepository.contarStockBajo()
        );

        dashboard.setInsumosAgotados(
                insumoRepository.listarAgotados().size()
        );

        // ======================================
        // ESTADISTICAS DE PROVEEDORES
        // ======================================

        dashboard.setTotalProveedores(
                proveedorRepository.contarTodos()
        );

        dashboard.setProveedoresActivos(
                proveedorRepository.contarActivos()
        );

        dashboard.setProveedoresInactivos(
                proveedorRepository.contarInactivos()
        );

        return dashboard;
    }

    // ==========================================
    // CONTAR PEDIDOS POR ESTADO
    // ==========================================
    private long contarPedidosPorEstado(
            List<Pedido> pedidos,
            String estado) {

        return pedidos.stream()
                .filter(pedido ->
                        pedido.getEstado() != null
                        && pedido.getEstado()
                                .equalsIgnoreCase(estado))
                .count();
    }

    // ==========================================
    // OBTENER MESERO AUTENTICADO
    // ==========================================
    private Usuario obtenerMeseroAutenticado() {

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

        Usuario usuario = usuarioRepository
                .buscarPorCorreo(autenticacion.getName())
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

        if (!"MESERO".equalsIgnoreCase(usuario.getRol())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo los meseros pueden consultar este dashboard"
            );
        }

        return usuario;
    }

    // ==========================================
    // DASHBOARD DEL MESERO
    // ==========================================
    public DashboardMeseroDTO obtenerDashboardMesero() {

        Usuario mesero = obtenerMeseroAutenticado();

        LocalDate hoy = LocalDate.now();

        // Obtener únicamente los pedidos del mesero
        List<Pedido> pedidos =
                pedidoRepository.buscarPorMesero(mesero.getId());

        // Filtrar los pedidos registrados hoy
        List<Pedido> pedidosHoy = pedidos.stream()
                .filter(pedido ->
                        pedido.getFecha() != null
                        && pedido.getFecha()
                                .toLocalDate()
                                .equals(hoy))
                .toList();

        DashboardMeseroDTO dashboard =
                new DashboardMeseroDTO();

        dashboard.setFecha(hoy);
        dashboard.setMeseroId(mesero.getId());

        dashboard.setTotalPedidos(pedidos.size());
        dashboard.setPedidosDelDia(pedidosHoy.size());

        // Contadores de pedidos del día
        dashboard.setPedidosPendientes(
                contarPedidosPorEstado(pedidosHoy, "PENDIENTE")
        );

        dashboard.setPedidosEnPreparacion(
                contarPedidosPorEstado(
                        pedidosHoy, "EN_PREPARACION")
        );

        dashboard.setPedidosListos(
                contarPedidosPorEstado(pedidosHoy, "LISTO")
        );

        dashboard.setPedidosEntregados(
                contarPedidosPorEstado(pedidosHoy, "ENTREGADO")
        );

        dashboard.setPedidosCancelados(
                contarPedidosPorEstado(pedidosHoy, "CANCELADO")
        );

        // Sumar el valor de los pedidos no cancelados
        BigDecimal montoGenerado = pedidosHoy.stream()
                .filter(pedido ->
                        pedido.getEstado() != null
                        && !"CANCELADO".equalsIgnoreCase(
                                pedido.getEstado()))
                .map(Pedido::getTotal)
                .filter(monto -> monto != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dashboard.setMontoGeneradoDelDia(montoGenerado);

        // Datos para graficas
        Map<String, Long> pedidosPorEstado =
                new LinkedHashMap<>();

        pedidosPorEstado.put(
                "PENDIENTE",
                dashboard.getPedidosPendientes()
        );

        pedidosPorEstado.put(
                "EN_PREPARACION",
                dashboard.getPedidosEnPreparacion()
        );

        pedidosPorEstado.put(
                "LISTO",
                dashboard.getPedidosListos()
        );

        pedidosPorEstado.put(
                "ENTREGADO",
                dashboard.getPedidosEntregados()
        );

        pedidosPorEstado.put(
                "CANCELADO",
                dashboard.getPedidosCancelados()
        );

        dashboard.setPedidosPorEstado(pedidosPorEstado);

        return dashboard;
    }

    // ==========================================
    // OBTENER CAJERO AUTENTICADO
    // ==========================================
    private Usuario obtenerCajeroAutenticado() {

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

        Usuario usuario = usuarioRepository
                .buscarPorCorreo(autenticacion.getName())
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

        if (!"CAJERO".equalsIgnoreCase(usuario.getRol())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo los cajeros pueden consultar este dashboard"
            );
        }

        return usuario;
    }

    // ==========================================
    // DASHBOARD DEL CAJERO
    // ==========================================
    public DashboardCajeroDTO obtenerDashboardCajero() {

        Usuario cajero = obtenerCajeroAutenticado();
        LocalDate hoy = LocalDate.now();

        DashboardCajeroDTO dashboard =
                new DashboardCajeroDTO();

        dashboard.setFecha(hoy);
        dashboard.setCajeroId(cajero.getId());

        // ======================================
        // PAGOS CONFIRMADOS DEL CAJERO
        // ======================================
        List<Pago> pagos = pagoRepository
                .buscarPorCajero(cajero.getId())
                .stream()
                .filter(pago ->
                        "PAGADO".equalsIgnoreCase(
                                pago.getEstado()))
                .toList();

        List<Pago> pagosHoy = pagos.stream()
                .filter(pago ->
                        pago.getFecha() != null
                        && pago.getFecha()
                                .toLocalDate()
                                .equals(hoy))
                .toList();

        dashboard.setTotalPagos(pagos.size());
        dashboard.setPagosDelDia(pagosHoy.size());

        // ======================================
        // INGRESOS TOTALES
        // ======================================
        BigDecimal ingresosTotales = pagos.stream()
                .map(Pago::getMonto)
                .filter(monto -> monto != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        dashboard.setIngresosTotales(ingresosTotales);

        // ======================================
        // INGRESOS DEL DIA
        // ======================================
        BigDecimal ingresosDelDia = pagosHoy.stream()
                .map(Pago::getMonto)
                .filter(monto -> monto != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        dashboard.setIngresosDelDia(ingresosDelDia);

        // ======================================
        // PEDIDOS PENDIENTES DE COBRO
        // ======================================
        long pendientesDeCobro =
                pedidoRepository.listarTodos()
                        .stream()
                        .filter(pedido ->
                                "ENTREGADO".equalsIgnoreCase(
                                        pedido.getEstado()))
                        .filter(pedido ->
                                pagoRepository
                                        .buscarPorPedido(
                                                pedido.getId())
                                        .stream()
                                        .noneMatch(pago ->
                                                "PAGADO".equalsIgnoreCase(
                                                        pago.getEstado())))
                        .count();

        dashboard.setPedidosPendientesDeCobro(
                pendientesDeCobro
        );

        // ======================================
        // PAGOS DEL DIA POR METODO
        // ======================================
        Map<String, Long> pagosPorMetodo =
                new LinkedHashMap<>();

        pagosPorMetodo.put("EFECTIVO", 0L);
        pagosPorMetodo.put("TARJETA", 0L);
        pagosPorMetodo.put("YAPE", 0L);
        pagosPorMetodo.put("PLIN", 0L);

        for (Pago pago : pagosHoy) {

            if (pago.getMetodoPago() != null) {

                String metodo = pago.getMetodoPago()
                        .toUpperCase();

                pagosPorMetodo.merge(
                        metodo,
                        1L,
                        Long::sum
                );
            }
        }

        dashboard.setPagosPorMetodo(pagosPorMetodo);

        return dashboard;
    }

}
