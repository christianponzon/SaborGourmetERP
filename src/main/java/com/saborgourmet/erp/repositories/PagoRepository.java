package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Pago;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PagoRepository {

    private final ConcurrentHashMap<Long, Pago> pagos =
            new ConcurrentHashMap<>();

    private final AtomicLong secuencia = new AtomicLong(1);

    // Guardar un pago
    public Pago guardar(Pago pago) {

        if (pago.getId() == null) {
            pago.setId(secuencia.getAndIncrement());
        }

        pagos.put(pago.getId(), pago);

        return pago;
    }

    // Listar todos los pagos
    public List<Pago> listarTodos() {
        return new ArrayList<>(pagos.values());
    }

    // Buscar un pago por su ID
    public Optional<Pago> buscarPorId(Long id) {
        return Optional.ofNullable(pagos.get(id));
    }

    // Buscar pagos asociados a un pedido
    public List<Pago> buscarPorPedido(Long pedidoId) {

        return pagos.values()
                .stream()
                .filter(pago ->
                        pedidoId.equals(pago.getPedidoId()))
                .toList();
    }

    // Buscar pagos registrados por un cajero
    public List<Pago> buscarPorCajero(Long cajeroId) {

        return pagos.values()
                .stream()
                .filter(pago ->
                        cajeroId.equals(pago.getCajeroId()))
                .toList();
    }

    // Buscar pagos por estado
    public List<Pago> buscarPorEstado(String estado) {

        return pagos.values()
                .stream()
                .filter(pago ->
                        estado.equalsIgnoreCase(pago.getEstado()))
                .toList();
    }

    // Comprobar si un pago existe
    public boolean existePorId(Long id) {
        return pagos.containsKey(id);
    }

    // Eliminar un pago
    public boolean eliminar(Long id) {
        return pagos.remove(id) != null;
    }
}
