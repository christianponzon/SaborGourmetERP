package com.saborgourmet.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class DashboardCajeroDTO {

    private LocalDate fecha;

    private Long cajeroId;

    private long totalPagos;
    private long pagosDelDia;

    private long pedidosPendientesDeCobro;

    private BigDecimal ingresosTotales;
    private BigDecimal ingresosDelDia;

    private Map<String, Long> pagosPorMetodo;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getCajeroId() {
        return cajeroId;
    }

    public void setCajeroId(Long cajeroId) {
        this.cajeroId = cajeroId;
    }

    public long getTotalPagos() {
        return totalPagos;
    }

    public void setTotalPagos(long totalPagos) {
        this.totalPagos = totalPagos;
    }

    public long getPagosDelDia() {
        return pagosDelDia;
    }

    public void setPagosDelDia(long pagosDelDia) {
        this.pagosDelDia = pagosDelDia;
    }

    public long getPedidosPendientesDeCobro() {
        return pedidosPendientesDeCobro;
    }

    public void setPedidosPendientesDeCobro(long pedidosPendientesDeCobro) {
        this.pedidosPendientesDeCobro = pedidosPendientesDeCobro;
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

    public Map<String, Long> getPagosPorMetodo() {
        return pagosPorMetodo;
    }

    public void setPagosPorMetodo(Map<String, Long> pagosPorMetodo) {
        this.pagosPorMetodo = pagosPorMetodo;
    }
}
