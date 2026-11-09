package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Entrega;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EntregaRepository {

    // Almacenamiento de entregas en memoria
    private final ConcurrentHashMap<Long, Entrega> entregas =
            new ConcurrentHashMap<>();

    // Generador automatico de IDs
    private final AtomicLong secuencia = new AtomicLong(1);

    // ==========================================
    // GUARDAR ENTREGA
    // ==========================================
    public Entrega guardar(Entrega entrega) {

        if (entrega.getId() == null) {
            entrega.setId(secuencia.getAndIncrement());
        }

        entregas.put(entrega.getId(), entrega);

        return entrega;
    }

    // ==========================================
    // LISTAR TODAS LAS ENTREGAS
    // ==========================================
    public List<Entrega> listarTodas() {

        return new ArrayList<>(entregas.values());
    }

    // ==========================================
    // BUSCAR ENTREGA POR ID
    // ==========================================
    public Optional<Entrega> buscarPorId(Long id) {

        return Optional.ofNullable(entregas.get(id));
    }

    // ==========================================
    // BUSCAR ENTREGA POR PEDIDO
    // ==========================================
    public Optional<Entrega> buscarPorPedido(Long pedidoId) {

        return entregas.values()
                .stream()
                .filter(entrega ->
                        pedidoId.equals(entrega.getPedidoId())
                )
                .findFirst();
    }

    // ==========================================
    // BUSCAR ENTREGAS POR REPARTIDOR
    // ==========================================
    public List<Entrega> buscarPorRepartidor(Long repartidorId) {

        return entregas.values()
                .stream()
                .filter(entrega ->
                        repartidorId.equals(entrega.getRepartidorId())
                )
                .toList();
    }

    // ==========================================
    // BUSCAR ENTREGAS POR ESTADO
    // ==========================================
    public List<Entrega> buscarPorEstado(String estado) {

        return entregas.values()
                .stream()
                .filter(entrega ->
                        estado.equalsIgnoreCase(entrega.getEstado())
                )
                .toList();
    }

    // ==========================================
    // COMPROBAR SI EXISTE UNA ENTREGA
    // ==========================================
    public boolean existePorId(Long id) {

        return entregas.containsKey(id);
    }

    // ==========================================
    // COMPROBAR SI UN PEDIDO TIENE ENTREGA
    // ==========================================
    public boolean existePorPedido(Long pedidoId) {

        return buscarPorPedido(pedidoId).isPresent();
    }

    // ==========================================
    // ELIMINAR ENTREGA
    // ==========================================
    public boolean eliminar(Long id) {

        return entregas.remove(id) != null;
    }
}
