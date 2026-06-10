package pe.tasa.modelo;

import java.time.LocalDateTime;

public class Auditoria {
    private int idAuditoria;
    private int idUsuario;
    private String tablaAfectada;
    private String accion;
    private String detalle;
    private LocalDateTime fecha;

    private Usuario usuario;

    public Auditoria() {}

    public Auditoria(int idUsuario, String tablaAfectada,
                     String accion, String detalle) {
        this.idUsuario = idUsuario;
        this.tablaAfectada = tablaAfectada;
        this.accion = accion;
        this.detalle = detalle;
    }

    public int getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(int idAuditoria) { this.idAuditoria = idAuditoria; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return "Auditoria{tabla='" + tablaAfectada + "', accion='" + accion + "', fecha=" + fecha + "}";
    }
}