package pe.tasa.modelo;

import java.time.LocalDate;

public class GuiaDespacho {
    private int idGuia;
    private int idPedido;
    private int idVehiculo;
    private int idChofer;
    private int idRuta;
    private LocalDate fechaSalida;
    private LocalDate fechaEntrega;
    private String estado;
    private String observaciones;

    private Pedido pedido;
    private Vehiculo vehiculo;
    private Chofer chofer;
    private Ruta ruta;

    public GuiaDespacho() {}

    public int getIdGuia() { return idGuia; }
    public void setIdGuia(int idGuia) { this.idGuia = idGuia; }
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
    public int getIdChofer() { return idChofer; }
    public void setIdChofer(int idChofer) { this.idChofer = idChofer; }
    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public Chofer getChofer() { return chofer; }
    public void setChofer(Chofer chofer) { this.chofer = chofer; }
    public Ruta getRuta() { return ruta; }
    public void setRuta(Ruta ruta) { this.ruta = ruta; }

    @Override
    public String toString() {
        return "GuiaDespacho{idGuia=" + idGuia + ", estado='" + estado + "'}";
    }
}