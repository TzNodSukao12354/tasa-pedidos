package pe.tasa.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionDB {

    private static final Logger LOG = Logger.getLogger(ConexionDB.class.getName());
    private static ConexionDB instancia;
    private Connection conexion;

    private ConexionDB() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String host     = dotenv.get("DB_HOST",     "localhost");
        String puerto   = dotenv.get("DB_PORT",     "5432");
        String nombre   = dotenv.get("DB_NOMBRE",   "tasa_pedidos");
        String usuario  = dotenv.get("DB_USUARIO",  "postgres");
        String password = dotenv.get("DB_PASSWORD", "");

        String url = String.format(
                "jdbc:postgresql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8",
                host, puerto, nombre
        );
        try {
            Class.forName("org.postgresql.Driver");
            this.conexion = DriverManager.getConnection(url, usuario, password);
            this.conexion.setAutoCommit(true);
            LOG.info("✔ Conexión establecida con PostgreSQL: " + nombre);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC no encontrado", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión a la base de datos", e);
        }
    }

    public static synchronized ConexionDB getInstancia() {
        try {
            if (instancia == null || instancia.conexion.isClosed()) {
                instancia = new ConexionDB();
            }
        } catch (SQLException e) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() { return this.conexion; }

    public void cerrar() {
        try {
            if (this.conexion != null && !this.conexion.isClosed()) {
                this.conexion.close();
                LOG.info("✔ Conexión cerrada.");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error al cerrar conexión", e);
        } finally {
            instancia = null;
        }
    }

    public void iniciarTransaccion() throws SQLException {
        this.conexion.setAutoCommit(false);
    }

    public void confirmar() throws SQLException {
        this.conexion.commit();
        this.conexion.setAutoCommit(true);
    }

    public void revertir() {
        try {
            this.conexion.rollback();
            this.conexion.setAutoCommit(true);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al revertir transacción", e);
        }
    }
}