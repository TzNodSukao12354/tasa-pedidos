package pe.tasa.dao;

import pe.tasa.modelo.DetallePedido;
import pe.tasa.modelo.Pedido;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAO implements DAO<Pedido, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    /**
     * Inserta el pedido y todos sus detalles en una sola transacción.
     * Si algo falla, se revierte todo.
     */
    @Override
    public void insertar(Pedido p) throws Exception {
        ConexionDB db = ConexionDB.getInstancia();
        db.iniciarTransaccion();
        try {
            // 1. Insertar cabecera del pedido
            String sqlPedido = "INSERT INTO Pedido (idEmpresa, idUsuario, fechaEntrega, estado, total, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = getConexion().prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getIdEmpresa());
                ps.setInt(2, p.getIdUsuario());
                ps.setDate(3, p.getFechaEntrega() != null ? Date.valueOf(p.getFechaEntrega()) : null);
                ps.setString(4, p.getEstado() != null ? p.getEstado() : "PENDIENTE");
                ps.setBigDecimal(5, p.getTotal());
                ps.setString(6, p.getObservaciones());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setIdPedido(rs.getInt(1));
                }
            }

            // 2. Insertar cada línea de detalle
            if (p.getDetalles() != null && !p.getDetalles().isEmpty()) {
                String sqlDetalle = "INSERT INTO DetallePedido (idPedido, idProducto, cantidad, precioUnitario, subtotal) " +
                        "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = getConexion().prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS)) {
                    for (DetallePedido d : p.getDetalles()) {
                        ps.setInt(1, p.getIdPedido());
                        ps.setInt(2, d.getIdProducto());
                        ps.setInt(3, d.getCantidad());
                        ps.setBigDecimal(4, d.getPrecioUnitario());
                        ps.setBigDecimal(5, d.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            db.confirmar();

        } catch (Exception e) {
            db.revertir();
            throw e;
        }
    }

    @Override
    public void actualizar(Pedido p) throws Exception {
        String sql = "UPDATE Pedido SET idEmpresa=?, idUsuario=?, fechaEntrega=?, " +
                "estado=?, total=?, observaciones=? WHERE idPedido=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, p.getIdEmpresa());
            ps.setInt(2, p.getIdUsuario());
            ps.setDate(3, p.getFechaEntrega() != null ? Date.valueOf(p.getFechaEntrega()) : null);
            ps.setString(4, p.getEstado());
            ps.setBigDecimal(5, p.getTotal());
            ps.setString(6, p.getObservaciones());
            ps.setInt(7, p.getIdPedido());
            ps.executeUpdate();
        }
    }

    public void actualizarEstado(int idPedido, String nuevoEstado) throws Exception {
        String sql = "UPDATE Pedido SET estado=? WHERE idPedido=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Pedido SET estado='ANULADO' WHERE idPedido=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT p.*, e.razonSocial, u.nombre AS nombreUsuario " +
                "FROM Pedido p " +
                "JOIN Empresa e ON p.idEmpresa = e.idEmpresa " +
                "JOIN Usuario u ON p.idUsuario = u.idUsuario " +
                "WHERE p.idPedido=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Pedido> listarTodos() throws Exception {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, e.razonSocial, u.nombre AS nombreUsuario " +
                "FROM Pedido p " +
                "JOIN Empresa e ON p.idEmpresa = e.idEmpresa " +
                "JOIN Usuario u ON p.idUsuario = u.idUsuario " +
                "ORDER BY p.fechaPedido DESC";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Pedido> listarPorEstado(String estado) throws Exception {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, e.razonSocial, u.nombre AS nombreUsuario " +
                "FROM Pedido p " +
                "JOIN Empresa e ON p.idEmpresa = e.idEmpresa " +
                "JOIN Usuario u ON p.idUsuario = u.idUsuario " +
                "WHERE p.estado=? ORDER BY p.fechaPedido DESC";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("idPedido"));
        p.setIdEmpresa(rs.getInt("idEmpresa"));
        p.setIdUsuario(rs.getInt("idUsuario"));
        p.setEstado(rs.getString("estado"));
        p.setTotal(rs.getBigDecimal("total"));
        p.setObservaciones(rs.getString("observaciones"));
        Timestamp ts = rs.getTimestamp("fechaPedido");
        if (ts != null) p.setFechaPedido(ts.toLocalDateTime());
        Date fe = rs.getDate("fechaEntrega");
        if (fe != null) p.setFechaEntrega(fe.toLocalDate());
        return p;
    }
}
