package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Entrega;
import com.saborgourmet.erp.models.Pedido;
import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.repositories.EntregaRepository;
import com.saborgourmet.erp.repositories.PedidoRepository;
import com.saborgourmet.erp.repositories.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public EntregaService(
            EntregaRepository entregaRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {

        this.entregaRepository = entregaRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ==========================================
    // LISTAR TODAS LAS ENTREGAS
    // ==========================================
    public List<Entrega> listarTodas() {
        return entregaRepository.listarTodas();
    }

    // ==========================================
    // BUSCAR ENTREGA POR ID
    // ==========================================
    public Entrega buscarPorId(Long id) {

        Entrega entrega = entregaRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Entrega no encontrada"
                        )
                );

        verificarAccesoEntrega(entrega);
        return entrega;
    }

    // ==========================================
    // BUSCAR ENTREGA POR PEDIDO
    // ==========================================
    public Entrega buscarPorPedido(Long pedidoId) {

        Entrega entrega = entregaRepository.buscarPorPedido(pedidoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Entrega no encontrada para este pedido"
                        )
                );

        verificarAccesoEntrega(entrega);
        return entrega;
    }

    // ==========================================
    // BUSCAR ENTREGAS POR REPARTIDOR
    // ==========================================
    public List<Entrega> buscarPorRepartidor(Long repartidorId) {

        Usuario usuario = obtenerUsuarioAutenticado();

        if (!"GERENTE".equals(usuario.getRol())
                && !usuario.getId().equals(repartidorId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes consultar entregas de otro repartidor"
            );
        }

        return entregaRepository.buscarPorRepartidor(repartidorId);
    }

    // ==========================================
    // BUSCAR ENTREGAS POR ESTADO
    // ==========================================
    public List<Entrega> buscarPorEstado(String estado) {

        String estadoNormalizado = normalizarEstado(estado);

        return entregaRepository.buscarPorEstado(estadoNormalizado);
    }

    // ==========================================
    // REGISTRAR ENTREGA
    // ==========================================
    public synchronized Entrega registrar(Entrega datos) {

        if (datos == null || datos.getPedidoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del pedido es obligatorio"
            );
        }

        Pedido pedido = pedidoRepository
                .buscarPorId(datos.getPedidoId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pedido no encontrado"
                        )
                );

        if (!"DELIVERY".equalsIgnoreCase(pedido.getTipo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden registrar entregas para pedidos DELIVERY"
            );
        }

        if ("CANCELADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede entregar un pedido cancelado"
            );
        }

        if (entregaRepository.existePorPedido(datos.getPedidoId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este pedido ya tiene una entrega registrada"
            );
        }

        if (datos.getDestinatario() == null
                || datos.getDestinatario().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El destinatario es obligatorio"
            );
        }

        if (datos.getDireccion() == null
                || datos.getDireccion().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La direccion es obligatoria"
            );
        }

        Entrega entrega = new Entrega();

        entrega.setPedidoId(pedido.getId());
        entrega.setDestinatario(datos.getDestinatario().trim());
        entrega.setTelefono(limpiarTexto(datos.getTelefono()));
        entrega.setDireccion(datos.getDireccion().trim());
        entrega.setReferencia(limpiarTexto(datos.getReferencia()));
        entrega.setObservaciones(
                limpiarTexto(datos.getObservaciones())
        );

        entrega.setEstado("PENDIENTE");
        entrega.setFechaCreacion(LocalDateTime.now());

        // El repartidor se asigna en una operacion separada
        entrega.setRepartidorId(null);
        entrega.setFechaAsignacion(null);
        entrega.setFechaEntrega(null);

        return entregaRepository.guardar(entrega);
    }

    // ==========================================
    // ASIGNAR REPARTIDOR
    // ==========================================
    public synchronized Entrega asignarRepartidor(
            Long entregaId,
            Long repartidorId) {

        Entrega entrega = obtenerEntrega(entregaId);

        if (repartidorId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del repartidor es obligatorio"
            );
        }

        if (!"PENDIENTE".equals(entrega.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden asignar entregas pendientes"
            );
        }

        Usuario repartidor = usuarioRepository
                .buscarPorId(repartidorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Repartidor no encontrado"
                        )
                );

        if (!repartidor.isActivo()
                || !"REPARTIDOR".equals(repartidor.getRol())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no es un repartidor activo"
            );
        }

        Pedido pedido = obtenerPedido(entrega.getPedidoId());

        if ("CANCELADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pedido fue cancelado"
            );
        }

        entrega.setRepartidorId(repartidor.getId());
        entrega.setEstado("ASIGNADO");
        entrega.setFechaAsignacion(LocalDateTime.now());

        return entregaRepository.guardar(entrega);
    }

    // ==========================================
    // CAMBIAR ESTADO DE ENTREGA
    // ==========================================
    public synchronized Entrega cambiarEstado(
            Long entregaId,
            String nuevoEstado) {

        Entrega entrega = obtenerEntrega(entregaId);

        Usuario usuario = obtenerUsuarioAutenticado();

        String estado = normalizarEstado(nuevoEstado);
        String estadoActual = entrega.getEstado();

        boolean esGerente = "GERENTE".equals(usuario.getRol());
        boolean esRepartidorAsignado =
                "REPARTIDOR".equals(usuario.getRol())
                && usuario.getId().equals(entrega.getRepartidorId());

        if (!esGerente && !esRepartidorAsignado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para modificar esta entrega"
            );
        }

        if (estado.equals(estadoActual)) {
            return entrega;
        }

        if ("ENTREGADO".equals(estadoActual)
                || "CANCELADO".equals(estadoActual)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta entrega ya esta finalizada"
            );
        }

        boolean transicionValida = switch (estadoActual) {

            case "PENDIENTE" ->
                    esGerente && "CANCELADO".equals(estado);

            case "ASIGNADO" ->
                    "EN_CAMINO".equals(estado)
                    || (esGerente && "CANCELADO".equals(estado));

            case "EN_CAMINO" ->
                    "ENTREGADO".equals(estado)
                    || (esGerente && "CANCELADO".equals(estado));

            default -> false;
        };

        if (!transicionValida) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cambio de estado no permitido"
            );
        }

        Pedido pedido = obtenerPedido(entrega.getPedidoId());

        if ("CANCELADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede avanzar una entrega de un pedido cancelado"
            );
        }

        // El pedido debe estar listo para salir a reparto
        if ("EN_CAMINO".equals(estado)
                && !"LISTO".equalsIgnoreCase(pedido.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pedido debe estar LISTO antes de salir a reparto"
            );
        }

        if ("ENTREGADO".equals(estado)) {

            if (!"LISTO".equalsIgnoreCase(pedido.getEstado())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El pedido debe estar LISTO para completar la entrega"
                );
            }

            // Actualizamos el pedido junto con la entrega
            pedido.setEstado("ENTREGADO");
            pedidoRepository.guardar(pedido);

            entrega.setFechaEntrega(LocalDateTime.now());
        }

        entrega.setEstado(estado);

        return entregaRepository.guardar(entrega);
    }

    // ==========================================
    // CANCELAR ENTREGA
    // ==========================================
    public synchronized Entrega cancelar(Long entregaId) {

        return cambiarEstado(entregaId, "CANCELADO");
    }

    // ==========================================
    // OBTENER ENTREGA
    // ==========================================
    private Entrega obtenerEntrega(Long id) {

        return entregaRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Entrega no encontrada"
                        )
                );
    }

    // ==========================================
    // OBTENER PEDIDO
    // ==========================================
    private Pedido obtenerPedido(Long id) {

        return pedidoRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pedido no encontrado"
                        )
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
                || !autenticacion.isAuthenticated()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        Usuario usuario = usuarioRepository
                .buscarPorCorreo(autenticacion.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuario no encontrado"
                        )
                );

        if (!usuario.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuario inactivo"
            );
        }

        return usuario;
    }

    // ==========================================
    // VERIFICAR ACCESO A ENTREGA
    // ==========================================
    private void verificarAccesoEntrega(Entrega entrega) {

        Usuario usuario = obtenerUsuarioAutenticado();

        if ("GERENTE".equals(usuario.getRol())) {
            return;
        }

        if ("REPARTIDOR".equals(usuario.getRol())
                && usuario.getId().equals(entrega.getRepartidorId())) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tienes permiso para consultar esta entrega"
        );
    }

    // ==========================================
    // NORMALIZAR ESTADO
    // ==========================================
    private String normalizarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        String valor = estado.trim().toUpperCase(Locale.ROOT);

        if (!valor.equals("PENDIENTE")
                && !valor.equals("ASIGNADO")
                && !valor.equals("EN_CAMINO")
                && !valor.equals("ENTREGADO")
                && !valor.equals("CANCELADO")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado de entrega no valido"
            );
        }

        return valor;
    }

    // ==========================================
    // LIMPIAR TEXTO
    // ==========================================
    private String limpiarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}
