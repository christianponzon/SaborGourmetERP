package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Reserva;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReservaRepository {

    // Almacenamiento de reservas en memoria
    private final ConcurrentHashMap<Long, Reserva> reservas =
            new ConcurrentHashMap<>();

    // Generador automático de IDs
    private final AtomicLong secuencia = new AtomicLong(1);

    // ==========================================
    // GUARDAR RESERVA
    // ==========================================
    public Reserva guardar(Reserva reserva) {

        if (reserva.getId() == null) {
            reserva.setId(secuencia.getAndIncrement());
        }

        reservas.put(reserva.getId(), reserva);

        return reserva;
    }

    // ==========================================
    // LISTAR TODAS LAS RESERVAS
    // ==========================================
    public List<Reserva> listarTodas() {

        return new ArrayList<>(reservas.values());
    }

    // ==========================================
    // BUSCAR RESERVA POR ID
    // ==========================================
    public Optional<Reserva> buscarPorId(Long id) {

        return Optional.ofNullable(reservas.get(id));
    }

    // ==========================================
    // BUSCAR RESERVAS POR FECHA
    // ==========================================
    public List<Reserva> buscarPorFecha(LocalDate fecha) {

        return reservas.values()
                .stream()
                .filter(reserva ->
                        fecha.equals(reserva.getFecha())
                )
                .toList();
    }

    // ==========================================
    // BUSCAR RESERVAS POR MESA
    // ==========================================
    public List<Reserva> buscarPorMesa(Long mesaId) {

        return reservas.values()
                .stream()
                .filter(reserva ->
                        mesaId.equals(reserva.getMesaId())
                )
                .toList();
    }

    // ==========================================
    // BUSCAR RESERVAS POR ESTADO
    // ==========================================
    public List<Reserva> buscarPorEstado(String estado) {

        return reservas.values()
                .stream()
                .filter(reserva ->
                        estado.equalsIgnoreCase(reserva.getEstado())
                )
                .toList();
    }

    // ==========================================
    // COMPROBAR SI EXISTE UNA RESERVA
    // ==========================================
    public boolean existePorId(Long id) {

        return reservas.containsKey(id);
    }

    // ==========================================
    // ELIMINAR RESERVA
    // ==========================================
    public boolean eliminar(Long id) {

        return reservas.remove(id) != null;
    }
}
