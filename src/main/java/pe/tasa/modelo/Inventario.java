package pe.tasa.modelo;

import java.time.LocalDateTime;

public class Inventario {
    private int idInventario;
    private int idProducto;
    private int idAlmacen;
    private String tipoMovimiento;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;

    // Objetos relacionados para joins
    private Producto producto;
    private Almacen almacen;

    public Inventario() {}

    public Inventario(int idInventario, int idProducto, int idAlmacen,
                      String tipoMovimiento, int cantidad, String motivo) {
        this.idInventario = idInventario;
        this.idProducto = idProducto;
        this.idAlmacen = idAlmacen;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    public int getIdInventario() { return idInventario; }
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(int idAlmacen) { this.idAlmacen = idAlmacen; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }

    @Override
    public String toString() {
        return "Inventario{idInventario=" + idInventario +
                ", tipoMovimiento='" + tipoMovimiento +
                "', cantidad=" + cantidad + "}";
    }
}