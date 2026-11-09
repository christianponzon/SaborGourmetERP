package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Pedido;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PedidoRepository {

    // Almacenamiento temporal de pedidos
    private final ConcurrentHashMap<Long, Pedido> pedidos =
            new ConcurrentHashMap<>();

    // Generador de IDs
    private final AtomicLong contador = new AtomicLong(0);

    // Guardar o actualizar un pedido
    public synchronized Pedido guardar(Pedido pedido) {

        if (pedido.getId() == null) {
            pedido.setId(contador.incrementAndGet());
        }

        pedidos.put(pedido.getId(), pedido);

        return pedido;
    }

    // Listar todos los pedidos
    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos.values());
    }

    // Buscar un pedido por ID
    public Optional<Pedido> buscarPorId(Long id) {

        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(pedidos.get(id));
    }

    // Buscar pedidos por estado
    public List<Pedido> buscarPorEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return listarTodos();
        }

        return pedidos.values()
                .stream()
                .filter(pedido ->
                    pedido.getEstado() != null &&
                    pedido.getEstado().equalsIgnoreCase(estado)
                )
                .toList();
    }

    // Buscar pedidos de una mesa
    public List<Pedido> buscarPorMesa(Long mesaId) {

        return pedidos.values()
                .stream()
                .filter(pedido ->
                    mesaId != null &&
                    mesaId.equals(pedido.getMesaId())
                )
                .toList();
    }

    // Buscar pedidos de un mesero
    public List<Pedido> buscarPorMesero(Long meseroId) {

        return pedidos.values()
                .stream()
                .filter(pedido ->
                    meseroId != null &&
                    meseroId.equals(pedido.getMeseroId())
                )
                .toList();
    }

    // Verificar si existe un pedido
    public boolean existePorId(Long id) {

        return id != null && pedidos.containsKey(id);
    }

    // Eliminar un pedido
    public boolean eliminar(Long id) {

        if (id == null) {
            return false;
        }

        return pedidos.remove(id) != null;
    }
}
