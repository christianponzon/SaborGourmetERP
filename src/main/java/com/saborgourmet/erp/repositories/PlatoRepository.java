package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Plato;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PlatoRepository {

    // Almacenamiento temporal en memoria
    private final ConcurrentHashMap<Long, Plato> platos =
            new ConcurrentHashMap<>();

    // Generador de identificadores
    private final AtomicLong contador = new AtomicLong(0);

    // Guardar o actualizar un plato
    public synchronized Plato guardar(Plato plato) {

        if (plato.getId() == null) {
            plato.setId(contador.incrementAndGet());
        }

        platos.put(plato.getId(), plato);

        return plato;
    }

    // Listar todos los platos
    public List<Plato> listarTodos() {
        return new ArrayList<>(platos.values());
    }

    // Buscar plato por ID
    public Optional<Plato> buscarPorId(Long id) {
        return Optional.ofNullable(platos.get(id));
    }

    // Buscar platos por categoria
    public List<Plato> buscarPorCategoria(String categoria) {

        if (categoria == null || categoria.isBlank()) {
            return listarTodos();
        }

        return platos.values()
                .stream()
                .filter(plato ->
                    plato.getCategoria() != null &&
                    plato.getCategoria().equalsIgnoreCase(categoria)
                )
                .toList();
    }

    // Listar platos disponibles
    public List<Plato> listarDisponibles() {

        return platos.values()
                .stream()
                .filter(Plato::isDisponible)
                .toList();
    }

    // Comprobar si existe un plato
    public boolean existePorId(Long id) {
        return platos.containsKey(id);
    }

    // Eliminar un plato
    public boolean eliminar(Long id) {
        return platos.remove(id) != null;
    }
}
