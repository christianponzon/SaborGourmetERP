package com.saborgourmet.erp.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private Long id;
    private Long mesaId;
    private Long meseroId;
    private String cliente;
    private String tipo;
    private String estado;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String observaciones;
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(
            Long id,
            Long mesaId,
            Long meseroId,
            String cliente,
            String tipo,
            String estado,
            LocalDateTime fecha,
            BigDecimal total,
            String observaciones,
            List<DetallePedido> detalles) {

        this.id = id;
        this.mesaId = mesaId;
        this.meseroId = meseroId;
        this.cliente = cliente;
        this.tipo = tipo;
        this.estado = estado;
        this.fecha = fecha;
        this.total = total;
        this.observaciones = observaciones;
        this.detalles = detalles != null
                ? detalles
                : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public Long getMeseroId() {
        return meseroId;
    }

    public void setMeseroId(Long meseroId) {
        this.meseroId = meseroId;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles != null
                ? detalles
                : new ArrayList<>();
    }
}
