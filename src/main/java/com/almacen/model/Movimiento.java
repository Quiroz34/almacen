package com.almacen.model;

import java.time.LocalDateTime;

public class Movimiento {
    private Long id;
    private Producto producto;
    private String tipo_movimiento;
    private Integer cantidad;
    private LocalDateTime fecha_movimiento;
    private Usuario usuario;

    public Movimiento() {
    }

    public Movimiento(Long id, Producto producto, String tipo_movimiento, Integer cantidad,
            LocalDateTime fecha_movimiento, Usuario usuario) {
        this.id = id;
        this.producto = producto;
        this.tipo_movimiento = tipo_movimiento;
        this.cantidad = cantidad;
        this.fecha_movimiento = fecha_movimiento;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getTipo_movimiento() {
        return tipo_movimiento;
    }

    public void setTipo_movimiento(String tipo_movimiento) {
        this.tipo_movimiento = tipo_movimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha_movimiento() {
        return fecha_movimiento;
    }

    public void setFecha_movimiento(LocalDateTime fecha_movimiento) {
        this.fecha_movimiento = fecha_movimiento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
