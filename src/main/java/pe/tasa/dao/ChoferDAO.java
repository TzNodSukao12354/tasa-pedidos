package pe.tasa.dao;

import pe.tasa.modelo.Chofer;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChoferDAO implements DAO<Chofer, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Chofer c) throws Exception {
        String sql = "INSERT INTO Chofer (dni, nombre, licencia, telefono, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getDni());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getLicencia());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdChofer(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Chofer c) throws Exception {
        String sql = "UPDATE Chofer SET nombre=?, licencia=?, telefono=?, estado=? WHERE idChofer=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getLicencia());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEstado());
            ps.setInt(5, c.getIdChofer());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Chofer SET estado='INACTIVO' WHERE idChofer=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Chofer> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Chofer WHERE idChofer=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Chofer> listarTodos() throws Exception {
        List<Chofer> lista = new ArrayList<>();
        String sql = "SELECT * FROM Chofer ORDER BY nombre";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Chofer> listarActivos() throws Exception {
        List<Chofer> lista = new ArrayList<>();
        String sql = "SELECT * FROM Chofer WHERE estado='ACTIVO' ORDER BY nombre";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Chofer mapear(ResultSet rs) throws SQLException {
        Chofer c = new Chofer();
        c.setIdChofer(rs.getInt("idChofer"));
        c.setDni(rs.getString("dni"));
        c.setNombre(rs.getString("nombre"));
        c.setLicencia(rs.getString("licencia"));
        c.setTelefono(rs.getString("telefono"));
        c.setEstado(rs.getString("estado"));
        return c;
    }
}