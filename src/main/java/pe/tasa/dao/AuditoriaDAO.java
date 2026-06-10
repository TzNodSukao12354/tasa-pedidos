package pe.tasa.dao;

import pe.tasa.modelo.Auditoria;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditoriaDAO implements DAO<Auditoria, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Auditoria a) throws Exception {
        String sql = "INSERT INTO Auditoria (idUsuario, tablaAfectada, accion, detalle) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, a.getIdUsuario());
            ps.setString(2, a.getTablaAfectada());
            ps.setString(3, a.getAccion());
            ps.setString(4, a.getDetalle());
            ps.executeUpdate();
        }
    }

    @Override public void actualizar(Auditoria a) throws Exception { /* Auditoria no se edita */ }

    @Override public void eliminar(Integer id) throws Exception { /* Auditoria no se elimina */ }

    @Override
    public Optional<Auditoria> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Auditoria WHERE idAuditoria=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Auditoria> listarTodos() throws Exception {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM Auditoria ORDER BY fecha DESC";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Auditoria mapear(ResultSet rs) throws SQLException {
        Auditoria a = new Auditoria();
        a.setIdAuditoria(rs.getInt("idAuditoria"));
        a.setIdUsuario(rs.getInt("idUsuario"));
        a.setTablaAfectada(rs.getString("tablaAfectada"));
        a.setAccion(rs.getString("accion"));
        a.setDetalle(rs.getString("detalle"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) a.setFecha(ts.toLocalDateTime());
        return a;
    }
}