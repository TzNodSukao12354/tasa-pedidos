package pe.tasa.modelo;

import java.math.BigDecimal;

public class Almacen {
    private int idAlmacen;
    private String nombre;
    private String direccion;
    private BigDecimal capacidad;
    private String estado;

    public Almacen() {}

    public Almacen(int idAlmacen, String nombre, String direccion,
                   BigDecimal capacidad, String estado) {
        this.idAlmacen = idAlmacen;
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(int idAlmacen) { this.idAlmacen = idAlmacen; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public BigDecimal getCapacidad() { return capacidad; }
    public void setCapacidad(BigDecimal capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Almacen{idAlmacen=" + idAlmacen + ", nombre='" + nombre + "'}";
    }
}