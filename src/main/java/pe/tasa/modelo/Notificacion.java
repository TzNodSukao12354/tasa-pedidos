package pe.tasa.modelo;

import java.time.LocalDateTime;

public class Notificacion {
    private int idNotificacion;
    private int idUsuario;
    private String titulo;
    private String mensaje;
    private boolean leido;
    private LocalDateTime fecha;

    private Usuario usuario;

    public Notificacion() {}

    public Notificacion(int idUsuario, String titulo, String mensaje) {
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leido = false;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return "Notificacion{titulo='" + titulo + "', leido=" + leido + "}";
    }
}