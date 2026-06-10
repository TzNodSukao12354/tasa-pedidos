package pe.tasa.modelo;

import java.math.BigDecimal;

public class Ruta {
    private int idRuta;
    private String nombre;
    private String zona;
    private BigDecimal distancia;

    public Ruta() {}

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public BigDecimal getDistancia() { return distancia; }
    public void setDistancia(BigDecimal distancia) { this.distancia = distancia; }

    @Override
    public String toString() {
        return "Ruta{nombre='" + nombre + "', zona='" + zona + "'}";
    }
}
