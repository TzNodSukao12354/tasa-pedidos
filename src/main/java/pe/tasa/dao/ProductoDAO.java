package pe.tasa.dao;

import pe.tasa.modelo.Producto;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAO implements DAO<Producto, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Producto p) throws Exception {
        String sql = "INSERT INTO Producto (codigo, nombre, descripcion, unidadMedida, precio, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setString(4, p.getUnidadMedida());
            ps.setBigDecimal(5, p.getPrecio());
            ps.setString(6, p.getEstado() != null ? p.getEstado() : "ACTIVO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdProducto(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Producto p) throws Exception {
        String sql = "UPDATE Producto SET codigo=?, nombre=?, descripcion=?, " +
                "unidadMedida=?, precio=?, estado=? WHERE idProducto=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setString(4, p.getUnidadMedida());
            ps.setBigDecimal(5, p.getPrecio());
            ps.setString(6, p.getEstado());
            ps.setInt(7, p.getIdProducto());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Producto SET estado='INACTIVO' WHERE idProducto=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Producto WHERE idProducto=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Producto> listarTodos() throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE estado='ACTIVO' ORDER BY nombre";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("idProducto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setUnidadMedida(rs.getString("unidadMedida"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setEstado(rs.getString("estado"));
        return p;
    }
}