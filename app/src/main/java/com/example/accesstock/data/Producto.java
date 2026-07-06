package com.example.accesstock.data;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Producto {

    private String id; // se usa el código de barras como ID del documento
    private String nombre;
    private String detalles;
    private int stock;
    private String codigoBarras;
    private String proveedor;

    @ServerTimestamp
    private Date fechaRegistro;

    public Producto() {
        // Constructor vacío requerido por Firestore
    }

    public Producto(String nombre, String detalles, int stock, String codigoBarras, String proveedor) {
        this.nombre = nombre;
        this.detalles = detalles;
        this.stock = stock;
        this.codigoBarras = codigoBarras;
        this.proveedor = proveedor;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}