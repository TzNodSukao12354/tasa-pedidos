package pe.tasa.dao;

import pe.tasa.modelo.Ruta;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RutaDAO implements DAO<Ruta, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Ruta r) throws Exception {
        String sql = "INSERT INTO Ruta (nombre, zona, distancia) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNombre());
            ps.setString(2, r.getZona());
            ps.setBigDecimal(3, r.getDistancia());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setIdRuta(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Ruta r) throws Exception {
        String sql = "UPDATE Ruta SET nombre=?, zona=?, distancia=? WHERE idRuta=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setString(2, r.getZona());
            ps.setBigDecimal(3, r.getDistancia());
            ps.setInt(4, r.getIdRuta());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "DELETE FROM Ruta WHERE idRuta=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Ruta> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Ruta WHERE idRuta=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Ruta> listarTodos() throws Exception {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ruta ORDER BY zona";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Ruta mapear(ResultSet rs) throws SQLException {
        Ruta r = new Ruta();
        r.setIdRuta(rs.getInt("idRuta"));
        r.setNombre(rs.getString("nombre"));
        r.setZona(rs.getString("zona"));
        r.setDistancia(rs.getBigDecimal("distancia"));
        return r;
    }
}