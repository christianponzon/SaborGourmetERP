package com.saborgourmet.erp.models;

import java.math.BigDecimal;

public class DetallePedido {

    private Long id;
    private Long platoId;
    private String nombrePlato;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String observaciones;

    public DetallePedido() {
    }

    public DetallePedido(
            Long id,
            Long platoId,
            String nombrePlato,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            String observaciones) {

        this.id = id;
        this.platoId = platoId;
        this.nombrePlato = nombrePlato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatoId() {
        return platoId;
    }

    public void setPlatoId(Long platoId) {
        this.platoId = platoId;
    }

    public String getNombrePlato() {
        return nombrePlato;
    }

    public void setNombrePlato(String nombrePlato) {
        this.nombrePlato = nombrePlato;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
