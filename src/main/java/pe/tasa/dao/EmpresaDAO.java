package pe.tasa.dao;

import pe.tasa.modelo.Empresa;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaDAO implements DAO<Empresa, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Empresa e) throws Exception {
        String sql = "INSERT INTO Empresa (ruc, razonSocial, telefono, direccion, correo, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getRuc());
            ps.setString(2, e.getRazonSocial());
            ps.setString(3, e.getTelefono());
            ps.setString(4, e.getDireccion());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getEstado() != null ? e.getEstado() : "ACTIVO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdEmpresa(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Empresa e) throws Exception {
        String sql = "UPDATE Empresa SET ruc=?, razonSocial=?, telefono=?, " +
                "direccion=?, correo=?, estado=? WHERE idEmpresa=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, e.getRuc());
            ps.setString(2, e.getRazonSocial());
            ps.setString(3, e.getTelefono());
            ps.setString(4, e.getDireccion());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getEstado());
            ps.setInt(7, e.getIdEmpresa());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Empresa SET estado='INACTIVO' WHERE idEmpresa=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Empresa> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Empresa WHERE idEmpresa=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Empresa> buscarPorRuc(String ruc) throws Exception {
        String sql = "SELECT * FROM Empresa WHERE ruc=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Empresa> listarTodos() throws Exception {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT * FROM Empresa WHERE estado='ACTIVO' ORDER BY razonSocial";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Empresa mapear(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setIdEmpresa(rs.getInt("idEmpresa"));
        e.setRuc(rs.getString("ruc"));
        e.setRazonSocial(rs.getString("razonSocial"));
        e.setTelefono(rs.getString("telefono"));
        e.setDireccion(rs.getString("direccion"));
        e.setCorreo(rs.getString("correo"));
        e.setEstado(rs.getString("estado"));
        return e;
    }
}