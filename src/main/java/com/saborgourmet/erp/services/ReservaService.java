package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Mesa;
import com.saborgourmet.erp.models.Reserva;
import com.saborgourmet.erp.repositories.MesaRepository;
import com.saborgourmet.erp.repositories.ReservaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;

    // Duracion estimada de cada reserva: 2 horas
    private static final Duration DURACION_RESERVA =
            Duration.ofHours(2);

    public ReservaService(
            ReservaRepository reservaRepository,
            MesaRepository mesaRepository) {

        this.reservaRepository = reservaRepository;
        this.mesaRepository = mesaRepository;
    }

    // ==========================================
    // LISTAR TODAS LAS RESERVAS
    // ==========================================
    public List<Reserva> listarTodas() {
        return reservaRepository.listarTodas();
    }

    // ==========================================
    // BUSCAR RESERVA POR ID
    // ==========================================
    public Reserva buscarPorId(Long id) {

        return reservaRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Reserva no encontrada"
                        )
                );
    }

    // ==========================================
    // BUSCAR RESERVAS POR FECHA
    // ==========================================
    public List<Reserva> buscarPorFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha es obligatoria"
            );
        }

        return reservaRepository.buscarPorFecha(fecha);
    }

    // ==========================================
    // BUSCAR RESERVAS POR MESA
    // ==========================================
    public List<Reserva> buscarPorMesa(Long mesaId) {

        mesaRepository.buscarPorId(mesaId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Mesa no encontrada"
                        )
                );

        return reservaRepository.buscarPorMesa(mesaId);
    }

    // ==========================================
    // BUSCAR RESERVAS POR ESTADO
    // ==========================================
    public List<Reserva> buscarPorEstado(String estado) {

        String estadoNormalizado = normalizarEstado(estado);

        return reservaRepository.buscarPorEstado(
                estadoNormalizado
        );
    }

    // ==========================================
    // REGISTRAR UNA RESERVA
    // ==========================================
    public synchronized Reserva registrar(Reserva datos) {

        if (datos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la reserva son obligatorios"
            );
        }

        // Validar mesa
        if (datos.getMesaId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID de la mesa es obligatorio"
            );
        }

        Mesa mesa = mesaRepository
                .buscarPorId(datos.getMesaId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Mesa no encontrada"
                        )
                );

        if (!mesa.isActiva()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La mesa esta desactivada"
            );
        }

        // Validar cliente
        if (datos.getCliente() == null
                || datos.getCliente().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del cliente es obligatorio"
            );
        }

        // Validar cantidad de personas
        if (datos.getCantidadPersonas() == null
                || datos.getCantidadPersonas() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad de personas debe ser mayor a cero"
            );
        }

        if (mesa.getCapacidad() == null
                || datos.getCantidadPersonas() > mesa.getCapacidad()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cantidad de personas supera la capacidad de la mesa"
            );
        }

        // Validar fecha y hora
        if (datos.getFecha() == null
                || datos.getHora() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha y la hora son obligatorias"
            );
        }

        LocalDateTime fechaHora = LocalDateTime.of(
                datos.getFecha(),
                datos.getHora()
        );

        if (!fechaHora.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La reserva debe tener una fecha y hora futura"
            );
        }

        // Verificar que no exista otra reserva
        // en un horario que se superponga
        verificarDisponibilidad(
                datos.getMesaId(),
                fechaHora,
                null
        );

        Reserva reserva = new Reserva();

        reserva.setMesaId(mesa.getId());
        reserva.setCliente(datos.getCliente().trim());

        reserva.setTelefono(
                limpiarTexto(datos.getTelefono())
        );

        reserva.setCorreo(
                limpiarTexto(datos.getCorreo())
        );

        reserva.setFecha(datos.getFecha());
        reserva.setHora(datos.getHora());
        reserva.setCantidadPersonas(
                datos.getCantidadPersonas()
        );

        reserva.setEstado("PENDIENTE");

        reserva.setObservaciones(
                limpiarTexto(datos.getObservaciones())
        );

        return reservaRepository.guardar(reserva);
    }

    // ==========================================
    // ACTUALIZAR UNA RESERVA
    // ==========================================
    public synchronized Reserva actualizar(
            Long id,
            Reserva datos) {

        Reserva reservaActual = buscarPorId(id);

        if ("CANCELADA".equalsIgnoreCase(
                reservaActual.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede actualizar una reserva cancelada"
            );
        }

        if (datos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos son obligatorios"
            );
        }

        if (datos.getMesaId() == null
                || datos.getFecha() == null
                || datos.getHora() == null
                || datos.getCantidadPersonas() == null
                || datos.getCliente() == null
                || datos.getCliente().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Faltan datos obligatorios"
            );
        }

        Mesa mesa = mesaRepository
                .buscarPorId(datos.getMesaId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Mesa no encontrada"
                        )
                );

        if (!mesa.isActiva()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La mesa esta desactivada"
            );
        }

        if (datos.getCantidadPersonas() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad de personas debe ser mayor a cero"
            );
        }

        if (mesa.getCapacidad() == null
                || datos.getCantidadPersonas() > mesa.getCapacidad()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cantidad de personas supera la capacidad de la mesa"
            );
        }

        LocalDateTime fechaHora = LocalDateTime.of(
                datos.getFecha(),
                datos.getHora()
        );

        if (!fechaHora.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha y hora deben ser futuras"
            );
        }

        verificarDisponibilidad(
                datos.getMesaId(),
                fechaHora,
                id
        );

        reservaActual.setMesaId(datos.getMesaId());
        reservaActual.setCliente(datos.getCliente().trim());
        reservaActual.setTelefono(
                limpiarTexto(datos.getTelefono())
        );
        reservaActual.setCorreo(
                limpiarTexto(datos.getCorreo())
        );
        reservaActual.setFecha(datos.getFecha());
        reservaActual.setHora(datos.getHora());
        reservaActual.setCantidadPersonas(
                datos.getCantidadPersonas()
        );
        reservaActual.setObservaciones(
                limpiarTexto(datos.getObservaciones())
        );

        return reservaRepository.guardar(reservaActual);
    }

    // ==========================================
    // CAMBIAR ESTADO DE RESERVA
    // ==========================================
    public synchronized Reserva cambiarEstado(
            Long id,
            String nuevoEstado) {

        Reserva reserva = buscarPorId(id);

        String estado = normalizarEstado(nuevoEstado);

        if ("CANCELADA".equalsIgnoreCase(
                reserva.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Una reserva cancelada no puede modificarse"
            );
        }

        if (estado.equals(reserva.getEstado())) {
            return reserva;
        }

        if (!estado.equals("CONFIRMADA")
                && !estado.equals("CANCELADA")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se puede confirmar o cancelar una reserva"
            );
        }

        if (estado.equals("CONFIRMADA")) {

            LocalDateTime fechaHora = LocalDateTime.of(
                    reserva.getFecha(),
                    reserva.getHora()
            );

            if (!fechaHora.isAfter(LocalDateTime.now())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No se puede confirmar una reserva pasada"
                );
            }

            verificarDisponibilidad(
                    reserva.getMesaId(),
                    fechaHora,
                    reserva.getId()
            );
        }

        reserva.setEstado(estado);

        return reservaRepository.guardar(reserva);
    }

    // ==========================================
    // CANCELAR RESERVA
    // ==========================================
    public synchronized Reserva cancelar(Long id) {

        return cambiarEstado(id, "CANCELADA");
    }

    // ==========================================
    // VERIFICAR DISPONIBILIDAD
    // ==========================================
    private void verificarDisponibilidad(
            Long mesaId,
            LocalDateTime inicio,
            Long reservaExcluirId) {

        LocalDateTime fin = inicio.plus(
                DURACION_RESERVA
        );

        boolean conflicto = reservaRepository
                .buscarPorMesa(mesaId)
                .stream()
                .filter(reserva ->
                        !"CANCELADA".equalsIgnoreCase(
                                reserva.getEstado()
                        )
                )
                .filter(reserva ->
                        reservaExcluirId == null
                        || !reservaExcluirId.equals(
                                reserva.getId()
                        )
                )
                .anyMatch(reserva -> {

                    LocalDateTime inicioExistente =
                            LocalDateTime.of(
                                    reserva.getFecha(),
                                    reserva.getHora()
                            );

                    LocalDateTime finExistente =
                            inicioExistente.plus(
                                    DURACION_RESERVA
                            );

                    return inicio.isBefore(finExistente)
                            && inicioExistente.isBefore(fin);
                });

        if (conflicto) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La mesa ya tiene una reserva en ese horario"
            );
        }
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

        String valor = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!valor.equals("PENDIENTE")
                && !valor.equals("CONFIRMADA")
                && !valor.equals("CANCELADA")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado de reserva no valido"
            );
        }

        return valor;
    }

    // ==========================================
    // LIMPIAR TEXTO OPCIONAL
    // ==========================================
    private String limpiarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}
