package pe.tasa.modelo;

public class Chofer {
    private int idChofer;
    private String dni;
    private String nombre;
    private String licencia;
    private String telefono;
    private String estado;

    public Chofer() {}

    public int getIdChofer() { return idChofer; }
    public void setIdChofer(int idChofer) { this.idChofer = idChofer; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Chofer{dni='" + dni + "', nombre='" + nombre + "'}";
    }
}
