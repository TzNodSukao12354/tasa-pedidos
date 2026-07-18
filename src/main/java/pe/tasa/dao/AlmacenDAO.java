package pe.tasa.dao;

import pe.tasa.modelo.Almacen;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlmacenDAO implements DAO<Almacen, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Almacen a) throws Exception {
        String sql = "INSERT INTO Almacen (nombre, direccion, capacidad, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getDireccion());
            ps.setBigDecimal(3, a.getCapacidad());
            ps.setString(4, a.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdAlmacen(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Almacen a) throws Exception {
        String sql = "UPDATE Almacen SET nombre=?, direccion=?, capacidad=?, estado=? WHERE idAlmacen=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getDireccion());
            ps.setBigDecimal(3, a.getCapacidad());
            ps.setString(4, a.getEstado());
            ps.setInt(5, a.getIdAlmacen());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Almacen SET estado='INACTIVO' WHERE idAlmacen=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Almacen> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Almacen WHERE idAlmacen=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Almacen> listarTodos() throws Exception {
        List<Almacen> lista = new ArrayList<>();
        String sql = "SELECT * FROM Almacen WHERE estado='ACTIVO' ORDER BY nombre";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Almacen mapear(ResultSet rs) throws SQLException {
        Almacen a = new Almacen();
        a.setIdAlmacen(rs.getInt("idAlmacen"));
        a.setNombre(rs.getString("nombre"));
        a.setDireccion(rs.getString("direccion"));
        a.setCapacidad(rs.getBigDecimal("capacidad"));
        a.setEstado(rs.getString("estado"));
        return a;
    }
}