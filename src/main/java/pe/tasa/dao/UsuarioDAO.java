package pe.tasa.dao;

import pe.tasa.modelo.Usuario;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements DAO<Usuario, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Usuario u) throws Exception {
        String sql = "INSERT INTO Usuario (nombre, correo, password, telefono, estado, idRol) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getEstado() != null ? u.getEstado() : "ACTIVO");
            ps.setInt(6, u.getIdRol());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Usuario u) throws Exception {
        String sql = "UPDATE Usuario SET nombre=?, correo=?, telefono=?, estado=?, idRol=? " +
                "WHERE idUsuario=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getEstado());
            ps.setInt(5, u.getIdRol());
            ps.setInt(6, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Usuario SET estado='INACTIVO' WHERE idUsuario=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Usuario WHERE idUsuario=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorCorreo(String correo) throws Exception {
        String sql = "SELECT * FROM Usuario WHERE correo=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario WHERE estado='ACTIVO' ORDER BY nombre";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("idUsuario"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setPassword(rs.getString("password"));
        u.setTelefono(rs.getString("telefono"));
        u.setEstado(rs.getString("estado"));
        u.setIdRol(rs.getInt("idRol"));
        Timestamp ts = rs.getTimestamp("fechaCreacion");
        if (ts != null) u.setFechaCreacion(ts.toLocalDateTime());
        return u;
    }
}