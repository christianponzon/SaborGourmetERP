package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Proveedor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProveedorRepository {

    // Almacenamiento en memoria
    private final ConcurrentHashMap<Long, Proveedor> proveedores =
            new ConcurrentHashMap<>();

    // Generador automático de ID
    private final AtomicLong secuencia = new AtomicLong(0);

    // Guardar o actualizar proveedor
    public Proveedor guardar(Proveedor proveedor) {

        if (proveedor.getId() == null) {
            proveedor.setId(secuencia.incrementAndGet());
        }

        proveedores.put(proveedor.getId(), proveedor);

        return proveedor;
    }

    // Listar todos los proveedores
    public List<Proveedor> listarTodos() {

        List<Proveedor> lista =
                new ArrayList<>(proveedores.values());

        lista.sort(Comparator.comparing(Proveedor::getId));

        return lista;
    }

    // Buscar proveedor por ID
    public Optional<Proveedor> buscarPorId(Long id) {
        return Optional.ofNullable(proveedores.get(id));
    }

    // Buscar proveedor por RUC
    public Optional<Proveedor> buscarPorRuc(String ruc) {

        return proveedores.values()
                .stream()
                .filter(p -> p.getRuc() != null)
                .filter(p -> p.getRuc().equals(ruc))
                .findFirst();
    }

    // Buscar proveedores por razón social
    public List<Proveedor> buscarPorRazonSocial(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            return listarTodos();
        }

        String busqueda = nombre.trim().toLowerCase();

        return listarTodos()
                .stream()
                .filter(p -> p.getRazonSocial() != null)
                .filter(p -> p.getRazonSocial()
                        .toLowerCase()
                        .contains(busqueda))
                .toList();
    }

    // Buscar proveedores por categoría
    public List<Proveedor> buscarPorCategoria(String categoria) {

        if (categoria == null || categoria.isBlank()) {
            return listarTodos();
        }

        return listarTodos()
                .stream()
                .filter(p -> p.getCategoria() != null)
                .filter(p -> p.getCategoria()
                        .equalsIgnoreCase(categoria.trim()))
                .toList();
    }

    // Listar proveedores activos
    public List<Proveedor> listarActivos() {

        return listarTodos()
                .stream()
                .filter(Proveedor::isActivo)
                .toList();
    }

    // Listar proveedores inactivos
    public List<Proveedor> listarInactivos() {

        return listarTodos()
                .stream()
                .filter(p -> !p.isActivo())
                .toList();
    }

    // Verificar si existe un proveedor por ID
    public boolean existePorId(Long id) {
        return proveedores.containsKey(id);
    }

    // Verificar si existe un proveedor por RUC
    public boolean existePorRuc(String ruc) {
        return buscarPorRuc(ruc).isPresent();
    }

    // Eliminar proveedor
    public boolean eliminar(Long id) {
        return proveedores.remove(id) != null;
    }

    // Contar todos los proveedores
    public long contarTodos() {
        return proveedores.size();
    }

    // Contar proveedores activos
    public long contarActivos() {

        return proveedores.values()
                .stream()
                .filter(Proveedor::isActivo)
                .count();
    }

    // Contar proveedores inactivos
    public long contarInactivos() {

        return proveedores.values()
                .stream()
                .filter(p -> !p.isActivo())
                .count();
    }
}
