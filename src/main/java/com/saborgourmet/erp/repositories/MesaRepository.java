package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Mesa;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MesaRepository {

    // Almacenamiento temporal en memoria
    private final ConcurrentHashMap<Long, Mesa> mesas =
            new ConcurrentHashMap<>();

    // Generador de identificadores
    private final AtomicLong contador = new AtomicLong(0);

    // Guardar o actualizar una mesa
    public synchronized Mesa guardar(Mesa mesa) {

        if (mesa.getId() == null) {
            mesa.setId(contador.incrementAndGet());
        }

        mesas.put(mesa.getId(), mesa);

        return mesa;
    }

    // Listar todas las mesas
    public List<Mesa> listarTodas() {
        return new ArrayList<>(mesas.values());
    }

    // Buscar una mesa por ID
    public Optional<Mesa> buscarPorId(Long id) {
        return Optional.ofNullable(mesas.get(id));
    }

    // Buscar una mesa por numero
    public Optional<Mesa> buscarPorNumero(Integer numero) {

        if (numero == null) {
            return Optional.empty();
        }

        return mesas.values()
                .stream()
                .filter(mesa ->
                    numero.equals(mesa.getNumero())
                )
                .findFirst();
    }

    // Listar mesas disponibles y activas
    public List<Mesa> listarDisponibles() {

        return mesas.values()
                .stream()
                .filter(mesa ->
                    mesa.isActiva() &&
                    "DISPONIBLE".equalsIgnoreCase(
                        mesa.getEstado()
                    )
                )
                .toList();
    }

    // Buscar mesas por estado
    public List<Mesa> buscarPorEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return listarTodas();
        }

        return mesas.values()
                .stream()
                .filter(mesa ->
                    mesa.getEstado() != null &&
                    mesa.getEstado().equalsIgnoreCase(estado)
                )
                .toList();
    }

    // Verificar si existe una mesa
    public boolean existePorId(Long id) {
        return id != null && mesas.containsKey(id);
    }

    // Eliminar una mesa
    public boolean eliminar(Long id) {

        if (id == null) {
            return false;
        }

        return mesas.remove(id) != null;
    }
}
