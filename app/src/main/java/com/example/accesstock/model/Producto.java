package com.example.accesstock.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Producto {

    private String id; // se llena después de guardar, no viaja a Firestore
    private String nombre;
    private String categoria;
    private long cantidad;
    private long stockMinimo;
    private String unidad;
    private String codigoBarras;
    @ServerTimestamp
    private Date fechaRegistro;
    private String registradoPor;

    // Constructor vacío obligatorio para Firestore
    public Producto() {}

    public Producto(String nombre, String categoria, long cantidad, long stockMinimo, String unidad, String registradoPor) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.unidad = unidad;
        this.registradoPor = registradoPor;
    }
    public Producto(String nombre, String categoria, long cantidad, long stockMinimo, String unidad, String registradoPor, String codigoBarras) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.unidad = unidad;
        this.registradoPor = registradoPor;
        this.codigoBarras = codigoBarras;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public long getCantidad() { return cantidad; }
    public void setCantidad(long cantidad) { this.cantidad = cantidad; }

    public long getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(long stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
}