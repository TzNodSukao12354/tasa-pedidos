package pe.tasa.modelo;

import java.time.LocalDateTime;

public class TicketEntrega {
    private int idTicket;
    private int idGuia;
    private LocalDateTime fechaGeneracion;
    private String nombreReceptor;
    private String firmaEvidencia;
    private String observaciones;
    private String estado;

    private GuiaDespacho guiaDespacho;

    public TicketEntrega() {}

    public int getIdTicket() { return idTicket; }
    public void setIdTicket(int idTicket) { this.idTicket = idTicket; }
    public int getIdGuia() { return idGuia; }
    public void setIdGuia(int idGuia) { this.idGuia = idGuia; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }
    public String getFirmaEvidencia() { return firmaEvidencia; }
    public void setFirmaEvidencia(String firmaEvidencia) { this.firmaEvidencia = firmaEvidencia; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public GuiaDespacho getGuiaDespacho() { return guiaDespacho; }
    public void setGuiaDespacho(GuiaDespacho guiaDespacho) { this.guiaDespacho = guiaDespacho; }

    @Override
    public String toString() {
        return "TicketEntrega{idTicket=" + idTicket + ", estado='" + estado + "'}";
    }
}