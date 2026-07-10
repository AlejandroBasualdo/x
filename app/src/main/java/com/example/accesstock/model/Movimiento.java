package com.example.accesstock.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Movimiento {

    private String idProducto;
    private String nombreProducto;
    private String tipoMovimiento; // "entrada", "salida", "eliminacion"
    private long cantidad;
    private String usuario;
    @ServerTimestamp
    private Date fecha;

    public Movimiento() {} // constructor vacío obligatorio para Firestore

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public long getCantidad() { return cantidad; }
    public void setCantidad(long cantidad) { this.cantidad = cantidad; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}