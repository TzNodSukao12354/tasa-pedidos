package pe.tasa.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    private int idPedido;
    private int idEmpresa;
    private int idUsuario;
    private LocalDateTime fechaPedido;
    private LocalDate fechaEntrega;
    private String estado;
    private BigDecimal total;
    private String observaciones;

    // Objetos relacionados para joins
    private Empresa empresa;
    private Usuario usuario;
    private List<DetallePedido> detalles;

    public Pedido() {}

    public Pedido(int idPedido, int idEmpresa, int idUsuario,
                  LocalDate fechaEntrega, String estado,
                  BigDecimal total, String observaciones) {
        this.idPedido = idPedido;
        this.idEmpresa = idEmpresa;
        this.idUsuario = idUsuario;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.total = total;
        this.observaciones = observaciones;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Pedido{idPedido=" + idPedido + ", estado='" + estado +
                "', total=" + total + "}";
    }
}