package pe.tasa.modelo;

public class Empresa {
    private int idEmpresa;
    private String ruc;
    private String razonSocial;
    private String telefono;
    private String direccion;
    private String correo;
    private String estado;

    public Empresa() {}

    public Empresa(int idEmpresa, String ruc, String razonSocial,
                   String telefono, String direccion, String correo, String estado) {
        this.idEmpresa = idEmpresa;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
        this.estado = estado;
    }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Empresa{idEmpresa=" + idEmpresa + ", ruc='" + ruc +
                "', razonSocial='" + razonSocial + "'}";
    }
}