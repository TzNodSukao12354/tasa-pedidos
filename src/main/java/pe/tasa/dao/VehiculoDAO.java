package pe.tasa.dao;

import pe.tasa.modelo.Vehiculo;
import pe.tasa.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiculoDAO implements DAO<Vehiculo, Integer> {

    private Connection getConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void insertar(Vehiculo v) throws Exception {
        String sql = "INSERT INTO Vehiculo (placa, marca, modelo, capacidad, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setBigDecimal(4, v.getCapacidad());
            ps.setString(5, v.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) v.setIdVehiculo(rs.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Vehiculo v) throws Exception {
        String sql = "UPDATE Vehiculo SET placa=?, marca=?, modelo=?, capacidad=?, estado=? WHERE idVehiculo=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setBigDecimal(4, v.getCapacidad());
            ps.setString(5, v.getEstado());
            ps.setInt(6, v.getIdVehiculo());
            ps.executeUpdate();
        }
    }

    public void actualizarEstado(int idVehiculo, String estado) throws Exception {
        String sql = "UPDATE Vehiculo SET estado=? WHERE idVehiculo=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idVehiculo);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        String sql = "UPDATE Vehiculo SET estado='MANTENIMIENTO' WHERE idVehiculo=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Vehiculo> buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM Vehiculo WHERE idVehiculo=?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Vehiculo> listarTodos() throws Exception {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Vehiculo ORDER BY placa";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Vehiculo> listarDisponibles() throws Exception {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Vehiculo WHERE estado='DISPONIBLE' ORDER BY placa";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Vehiculo mapear(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getInt("idVehiculo"));
        v.setPlaca(rs.getString("placa"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setCapacidad(rs.getBigDecimal("capacidad"));
        v.setEstado(rs.getString("estado"));
        return v;
    }
}