package com.saborgourmet.erp.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {

    private Long id;
    private Long pedidoId;
    private Long cajeroId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String referencia;
    private LocalDateTime fecha;

    public Pago() {
    }

    public Pago(Long id, Long pedidoId, Long cajeroId,
                BigDecimal monto, String metodoPago,
                String estado, String referencia,
                LocalDateTime fecha) {

        this.id = id;
        this.pedidoId = pedidoId;
        this.cajeroId = cajeroId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.referencia = referencia;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getCajeroId() {
        return cajeroId;
    }

    public void setCajeroId(Long cajeroId) {
        this.cajeroId = cajeroId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
