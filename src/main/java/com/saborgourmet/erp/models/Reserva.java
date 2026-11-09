package com.saborgourmet.erp.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

    private Long id;
    private Long mesaId;
    private String cliente;
    private String telefono;
    private String correo;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer cantidadPersonas;
    private String estado;
    private String observaciones;

    public Reserva() {
    }

    public Reserva(
            Long id,
            Long mesaId,
            String cliente,
            String telefono,
            String correo,
            LocalDate fecha,
            LocalTime hora,
            Integer cantidadPersonas,
            String estado,
            String observaciones) {

        this.id = id;
        this.mesaId = mesaId;
        this.cliente = cliente;
        this.telefono = telefono;
        this.correo = correo;
        this.fecha = fecha;
        this.hora = hora;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = estado;
        this.observaciones = observaciones;
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

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Integer getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
