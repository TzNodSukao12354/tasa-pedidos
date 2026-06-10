package pe.tasa.dao;

import pe.tasa.modelo.Rol;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RolDAO implements DAO<Rol, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Rol rol) throws Exception {
        String sql = "INSERT INTO Rol (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rol.getNombre());
            ps.setString(2, rol.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) rol.setIdRol(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Rol rol) throws Exception {
        String sql = "UPDATE Rol SET nombre=?, descripcion=? WHERE idRol=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, rol.getNombre());
            ps.setString(2, rol.getDescripcion());
            ps.setInt(3, rol.getIdRol());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "DELETE FROM Rol WHERE idRol=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Rol> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Rol WHERE idRol=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Rol> listarTodos() throws Exception {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM Rol ORDER BY idRol";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Rol mapear(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setIdRol(rs.getInt("idRol"));
        r.setNombre(rs.getString("nombre"));
        r.setDescripcion(rs.getString("descripcion"));
        return r;
    }
}