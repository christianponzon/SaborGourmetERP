package com.saborgourmet.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class DashboardMeseroDTO {

    private LocalDate fecha;
    private Long meseroId;

    private long totalPedidos;
    private long pedidosDelDia;
    private long pedidosPendientes;
    private long pedidosEnPreparacion;
    private long pedidosListos;
    private long pedidosEntregados;
    private long pedidosCancelados;

    private BigDecimal montoGeneradoDelDia;

    // Datos para graficas: estado y cantidad
    private Map<String, Long> pedidosPorEstado;

    public DashboardMeseroDTO() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getMeseroId() {
        return meseroId;
    }

    public void setMeseroId(Long meseroId) {
        this.meseroId = meseroId;
    }

    public long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public long getPedidosDelDia() {
        return pedidosDelDia;
    }

    public void setPedidosDelDia(long pedidosDelDia) {
        this.pedidosDelDia = pedidosDelDia;
    }

    public long getPedidosPendientes() {
        return pedidosPendientes;
    }

    public void setPedidosPendientes(long pedidosPendientes) {
        this.pedidosPendientes = pedidosPendientes;
    }

    public long getPedidosEnPreparacion() {
        return pedidosEnPreparacion;
    }

    public void setPedidosEnPreparacion(long pedidosEnPreparacion) {
        this.pedidosEnPreparacion = pedidosEnPreparacion;
    }

    public long getPedidosListos() {
        return pedidosListos;
    }

    public void setPedidosListos(long pedidosListos) {
        this.pedidosListos = pedidosListos;
    }

    public long getPedidosEntregados() {
        return pedidosEntregados;
    }

    public void setPedidosEntregados(long pedidosEntregados) {
        this.pedidosEntregados = pedidosEntregados;
    }

    public long getPedidosCancelados() {
        return pedidosCancelados;
    }

    public void setPedidosCancelados(long pedidosCancelados) {
        this.pedidosCancelados = pedidosCancelados;
    }

    public BigDecimal getMontoGeneradoDelDia() {
        return montoGeneradoDelDia;
    }

    public void setMontoGeneradoDelDia(
            BigDecimal montoGeneradoDelDia) {
        this.montoGeneradoDelDia = montoGeneradoDelDia;
    }

    public Map<String, Long> getPedidosPorEstado() {
        return pedidosPorEstado;
    }

    public void setPedidosPorEstado(
            Map<String, Long> pedidosPorEstado) {
        this.pedidosPorEstado = pedidosPorEstado;
    }
}
