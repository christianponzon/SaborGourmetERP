package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Insumo;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InsumoRepository {

    // Almacenamiento de insumos en memoria
    private final ConcurrentHashMap<Long, Insumo> insumos =
            new ConcurrentHashMap<>();

    // Generador automatico de IDs
    private final AtomicLong secuencia = new AtomicLong(1);

    // ==========================================
    // GUARDAR INSUMO
    // ==========================================
    public Insumo guardar(Insumo insumo) {

        if (insumo.getId() == null) {
            insumo.setId(secuencia.getAndIncrement());
        }

        insumos.put(insumo.getId(), insumo);

        return insumo;
    }

    // ==========================================
    // LISTAR TODOS LOS INSUMOS
    // ==========================================
    public List<Insumo> listarTodos() {

        return new ArrayList<>(insumos.values());
    }

    // ==========================================
    // BUSCAR INSUMO POR ID
    // ==========================================
    public Optional<Insumo> buscarPorId(Long id) {

        return Optional.ofNullable(insumos.get(id));
    }

    // ==========================================
    // BUSCAR INSUMO POR NOMBRE
    // ==========================================
    public Optional<Insumo> buscarPorNombre(String nombre) {

        if (nombre == null) {
            return Optional.empty();
        }

        return insumos.values()
                .stream()
                .filter(insumo ->
                        insumo.getNombre() != null
                        && insumo.getNombre()
                                .equalsIgnoreCase(nombre.trim())
                )
                .findFirst();
    }

    // ==========================================
    // BUSCAR INSUMOS POR CATEGORIA
    // ==========================================
    public List<Insumo> buscarPorCategoria(String categoria) {

        if (categoria == null) {
            return List.of();
        }

        return insumos.values()
                .stream()
                .filter(insumo ->
                        insumo.getCategoria() != null
                        && insumo.getCategoria()
                                .equalsIgnoreCase(categoria.trim())
                )
                .toList();
    }

    // ==========================================
    // LISTAR INSUMOS ACTIVOS
    // ==========================================
    public List<Insumo> listarActivos() {

        return insumos.values()
                .stream()
                .filter(Insumo::isActivo)
                .toList();
    }

    // ==========================================
    // LISTAR INSUMOS CON STOCK BAJO
    // ==========================================
    public List<Insumo> listarStockBajo() {

        return insumos.values()
                .stream()
                .filter(Insumo::isActivo)
                .filter(insumo ->
                        insumo.getStockActual() != null
                        && insumo.getStockMinimo() != null
                        && insumo.getStockActual()
                                .compareTo(insumo.getStockMinimo()) <= 0
                )
                .toList();
    }

    // ==========================================
    // LISTAR INSUMOS AGOTADOS
    // ==========================================
    public List<Insumo> listarAgotados() {

        return insumos.values()
                .stream()
                .filter(Insumo::isActivo)
                .filter(insumo ->
                        insumo.getStockActual() != null
                        && insumo.getStockActual()
                                .compareTo(BigDecimal.ZERO) == 0
                )
                .toList();
    }

    // ==========================================
    // VERIFICAR SI EXISTE UN INSUMO
    // ==========================================
    public boolean existePorId(Long id) {

        return insumos.containsKey(id);
    }

    // ==========================================
    // VERIFICAR SI EXISTE POR NOMBRE
    // ==========================================
    public boolean existePorNombre(String nombre) {

        return buscarPorNombre(nombre).isPresent();
    }

    // ==========================================
    // ELIMINAR INSUMO
    // ==========================================
    public boolean eliminar(Long id) {

        return insumos.remove(id) != null;
    }

    // ==========================================
    // CONTAR TODOS LOS INSUMOS
    // ==========================================
    public long contarTodos() {

        return insumos.size();
    }

    // ==========================================
    // CONTAR INSUMOS CON STOCK BAJO
    // ==========================================
    public long contarStockBajo() {

        return listarStockBajo().size();
    }
}
