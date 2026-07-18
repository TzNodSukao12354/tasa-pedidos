package pe.tasa.dao;

import pe.tasa.modelo.Inventario;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h2>InventarioDAO</h2>
 * Gestiona los movimientos de stock (ENTRADA, SALIDA, AJUSTE).
 *
 * @author TASA
 * @version 1.0
 */
public class InventarioDAO implements DAO<Inventario, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Inventario i) throws Exception {
        String sql = "INSERT INTO Inventario (idProducto, idAlmacen, tipoMovimiento, cantidad, motivo) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i.getIdProducto());
            ps.setInt(2, i.getIdAlmacen());
            ps.setString(3, i.getTipoMovimiento());
            ps.setInt(4, i.getCantidad());
            ps.setString(5, i.getMotivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setIdInventario(rs.getInt(1));
            }
        }
    }

    @Override public void actualizar(Inventario i) throws Exception { /* no se edita */ }
    @Override public void eliminar(Integer id) throws Exception { /* no se elimina */ }

    @Override
    public Optional<Inventario> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Inventario WHERE idInventario=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Inventario> listarTodos() throws Exception {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT i.*, p.nombre AS nombreProducto, a.nombre AS nombreAlmacen " +
                "FROM Inventario i " +
                "JOIN Producto p ON i.idProducto = p.idProducto " +
                "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                "ORDER BY i.fecha DESC";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /**
     * Calcula el stock actual de un producto sumando ENTRADAS
     * y restando SALIDAS en todos los almacenes.
     */
    public int calcularStockActual(int idProducto) throws Exception {
        String sql = "SELECT " +
                "COALESCE(SUM(CASE WHEN tipoMovimiento='ENTRADA' THEN cantidad ELSE 0 END), 0) - " +
                "COALESCE(SUM(CASE WHEN tipoMovimiento='SALIDA' THEN cantidad ELSE 0 END), 0) AS stock " +
                "FROM Inventario WHERE idProducto = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
            }
        }
        return 0;
    }

    private Inventario mapear(ResultSet rs) throws SQLException {
        Inventario i = new Inventario();
        i.setIdInventario(rs.getInt("idInventario"));
        i.setIdProducto(rs.getInt("idProducto"));
        i.setIdAlmacen(rs.getInt("idAlmacen"));
        i.setTipoMovimiento(rs.getString("tipoMovimiento"));
        i.setCantidad(rs.getInt("cantidad"));
        i.setMotivo(rs.getString("motivo"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) i.setFecha(ts.toLocalDateTime());

        try {
            pe.tasa.modelo.Producto p = new pe.tasa.modelo.Producto();
            p.setNombre(rs.getString("nombreProducto"));
            i.setProducto(p);

            pe.tasa.modelo.Almacen a = new pe.tasa.modelo.Almacen();
            a.setNombre(rs.getString("nombreAlmacen"));
            i.setAlmacen(a);
        } catch (SQLException ignored) {}

        return i;
    }
}