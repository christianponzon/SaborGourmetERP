package com.saborgourmet.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DashboardGerenteDTO {

    private LocalDate fecha;

    // PEDIDOS
    private long totalPedidos;
    private long pedidosDelDia;
    private long pedidosPendientes;
    private long pedidosEnPreparacion;
    private long pedidosEntregados;
    private long pedidosCancelados;

    // INGRESOS
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosDelDia;

    // MESAS
    private long totalMesas;
    private long mesasDisponibles;
    private long mesasOcupadas;
    private long mesasReservadas;

    // PLATOS
    private long totalPlatos;
    private long platosDisponibles;

    // INVENTARIO
    private long totalInsumos;
    private long insumosActivos;
    private long insumosStockBajo;
    private long insumosAgotados;

    // PROVEEDORES
    private long totalProveedores;
    private long proveedoresActivos;
    private long proveedoresInactivos;

    public DashboardGerenteDTO() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

    public BigDecimal getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(BigDecimal ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }

    public BigDecimal getIngresosDelDia() {
        return ingresosDelDia;
    }

    public void setIngresosDelDia(BigDecimal ingresosDelDia) {
        this.ingresosDelDia = ingresosDelDia;
    }

    public long getTotalMesas() {
        return totalMesas;
    }

    public void setTotalMesas(long totalMesas) {
        this.totalMesas = totalMesas;
    }

    public long getMesasDisponibles() {
        return mesasDisponibles;
    }

    public void setMesasDisponibles(long mesasDisponibles) {
        this.mesasDisponibles = mesasDisponibles;
    }

    public long getMesasOcupadas() {
        return mesasOcupadas;
    }

    public void setMesasOcupadas(long mesasOcupadas) {
        this.mesasOcupadas = mesasOcupadas;
    }

    public long getMesasReservadas() {
        return mesasReservadas;
    }

    public void setMesasReservadas(long mesasReservadas) {
        this.mesasReservadas = mesasReservadas;
    }

    public long getTotalPlatos() {
        return totalPlatos;
    }

    public void setTotalPlatos(long totalPlatos) {
        this.totalPlatos = totalPlatos;
    }

    public long getPlatosDisponibles() {
        return platosDisponibles;
    }

    public void setPlatosDisponibles(long platosDisponibles) {
        this.platosDisponibles = platosDisponibles;
    }

    public long getTotalInsumos() {
        return totalInsumos;
    }

    public void setTotalInsumos(long totalInsumos) {
        this.totalInsumos = totalInsumos;
    }

    public long getInsumosActivos() {
        return insumosActivos;
    }

    public void setInsumosActivos(long insumosActivos) {
        this.insumosActivos = insumosActivos;
    }

    public long getInsumosStockBajo() {
        return insumosStockBajo;
    }

    public void setInsumosStockBajo(long insumosStockBajo) {
        this.insumosStockBajo = insumosStockBajo;
    }

    public long getInsumosAgotados() {
        return insumosAgotados;
    }

    public void setInsumosAgotados(long insumosAgotados) {
        this.insumosAgotados = insumosAgotados;
    }

    public long getTotalProveedores() {
        return totalProveedores;
    }

    public void setTotalProveedores(long totalProveedores) {
        this.totalProveedores = totalProveedores;
    }

    public long getProveedoresActivos() {
        return proveedoresActivos;
    }

    public void setProveedoresActivos(long proveedoresActivos) {
        this.proveedoresActivos = proveedoresActivos;
    }

    public long getProveedoresInactivos() {
        return proveedoresInactivos;
    }

    public void setProveedoresInactivos(long proveedoresInactivos) {
        this.proveedoresInactivos = proveedoresInactivos;
    }
}
