package pe.tasa.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import pe.tasa.dao.*;
import pe.tasa.modelo.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

/**
 * <h2>AsignarDespachoApp</h2>
 * Pantalla del Admin para asignar Chofer, Vehículo y Ruta
 * a un pedido confirmado, generando la GuiaDespacho.
 *
 * @author TASA
 * @version 1.0
 */
public class AsignarDespachoApp {

    private Usuario usuarioActual;
    private Pedido pedido;

    public AsignarDespachoApp(Usuario usuario, Pedido pedido) {
        this.usuarioActual = usuario;
        this.pedido = pedido;
    }

    public void show(Stage stage) {

        // ── Barra superior ────────────────────────────
        HBox barraTop = new HBox();
        barraTop.setPadding(new Insets(15, 20, 15, 20));
        barraTop.setAlignment(Pos.CENTER_LEFT);
        barraTop.setStyle("-fx-background-color: #b71c1c;");

        Label lblTitulo = new Label("TASA — Asignar Despacho (Pedido #" + pedido.getIdPedido() + ")");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Button btnVolver = new Button("← Volver");
        btnVolver.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: white; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 6 12 6 12;");

        barraTop.getChildren().addAll(lblTitulo, espaciador, btnVolver);

        // ── Formulario ────────────────────────────────
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f5f5f5;");

        VBox formulario = new VBox(15);
        formulario.setPadding(new Insets(25, 30, 25, 30));
        formulario.setStyle("-fx-background-color: #f5f5f5;");
        formulario.setAlignment(Pos.CENTER_LEFT); // ✅ Alineado a la izquierda

        // Resumen del pedido
        Label lblInfoPedido = new Label(
                "Pedido #" + pedido.getIdPedido() +
                        " — Total: S/ " + pedido.getTotal() +
                        " — Cliente: " + (pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : "-") +
                        " — Empresa: " + (pedido.getEmpresa() != null ? pedido.getEmpresa().getRazonSocial() : "-"));
        lblInfoPedido.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lblInfoPedido.setWrapText(true);
        lblInfoPedido.setStyle(
                "-fx-background-color: #e8eaf6; " +
                        "-fx-padding: 12; " +
                        "-fx-background-radius: 5;");
        lblInfoPedido.setMaxWidth(Double.MAX_VALUE);

        // Chofer
        Label lblChofer = new Label("Seleccionar Chofer *");
        lblChofer.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<Chofer> cmbChofer = new ComboBox<>();
        cmbChofer.setPrefWidth(400);
        cmbChofer.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Chofer c) {
                return c == null ? "" : c.getNombre() + " — DNI: " + c.getDni();
            }
            @Override public Chofer fromString(String s) { return null; }
        });

        // Vehículo
        Label lblVehiculo = new Label("Seleccionar Vehículo *");
        lblVehiculo.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<Vehiculo> cmbVehiculo = new ComboBox<>();
        cmbVehiculo.setPrefWidth(400);
        cmbVehiculo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Vehiculo v) {
                return v == null ? "" : v.getPlaca() + " — " + v.getMarca() + " " + v.getModelo();
            }
            @Override public Vehiculo fromString(String s) { return null; }
        });

        // Ruta
        Label lblRuta = new Label("Seleccionar Ruta/Zona *");
        lblRuta.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<Ruta> cmbRuta = new ComboBox<>();
        cmbRuta.setPrefWidth(400);
        cmbRuta.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Ruta r) {
                return r == null ? "" : r.getNombre() + " (" + r.getDistancia() + " km)";
            }
            @Override public Ruta fromString(String s) { return null; }
        });

        // Cargar datos
        try {
            cmbChofer.getItems().addAll(new ChoferDAO().listarActivos());
            cmbVehiculo.getItems().addAll(new VehiculoDAO().listarDisponibles());
            cmbRuta.getItems().addAll(new RutaDAO().listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fecha salida
        Label lblFechaSalida = new Label("Fecha de Salida *");
        lblFechaSalida.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        DatePicker dateSalida = new DatePicker(LocalDate.now());
        dateSalida.setPrefWidth(400);

        // Mensaje
        Label lblMensaje = new Label("");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lblMensaje.setWrapText(true);

        // Botón generar guía
        Button btnGenerar = new Button("✔ GENERAR GUÍA DE DESPACHO");
        btnGenerar.setPrefWidth(250); // ✅ Ancho consistente
        btnGenerar.setPrefHeight(45);
        btnGenerar.setStyle(
                "-fx-background-color: #2e7d32; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10 20 10 20;");

        // ── Acción generar ─────────────────────────────
        btnGenerar.setOnAction(e -> {
            if (cmbChofer.getValue() == null) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Selecciona un chofer.");
                return;
            }
            if (cmbVehiculo.getValue() == null) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Selecciona un vehículo.");
                return;
            }
            if (cmbRuta.getValue() == null) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Selecciona una ruta.");
                return;
            }

            try {
                Chofer chofer     = cmbChofer.getValue();
                Vehiculo vehiculo = cmbVehiculo.getValue();
                Ruta ruta         = cmbRuta.getValue();

                // 1. Insertar GuiaDespacho — queda esperando que el chofer acepte
                String sql = "INSERT INTO GuiaDespacho (idPedido, idVehiculo, idChofer, idRuta, " +
                        "fechaSalida, estado) VALUES (?, ?, ?, ?, ?, 'PENDIENTE_ACEPTACION')";
                try (PreparedStatement ps = pe.tasa.util.ConexionDB.getInstancia()
                        .getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pedido.getIdPedido());
                    ps.setInt(2, vehiculo.getIdVehiculo());
                    ps.setInt(3, chofer.getIdChofer());
                    ps.setInt(4, ruta.getIdRuta());
                    ps.setDate(5, java.sql.Date.valueOf(dateSalida.getValue()));
                    ps.executeUpdate();
                }

                // 2. NO cambiamos el pedido todavía — espera que el chofer acepte
                new AuditoriaDAO().insertar(new Auditoria(
                        usuarioActual.getIdUsuario(), "Pedido", "UPDATE",
                        "Pedido #" + pedido.getIdPedido() + " — Guía asignada a " +
                                chofer.getNombre() + ", esperando aceptación"
                ));

                lblMensaje.setTextFill(Color.GREEN);
                lblMensaje.setText("✔ Guía enviada a " + chofer.getNombre() +
                        ". Esperando que el chofer acepte el viaje.");

            } catch (Exception ex) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // ── Acción volver ─────────────────────────────
        btnVolver.setOnAction(e ->
                new DashboardAdminApp(usuarioActual).show(stage));

        formulario.getChildren().addAll(
                lblInfoPedido,
                new Separator(),
                lblChofer, cmbChofer,
                lblVehiculo, cmbVehiculo,
                lblRuta, cmbRuta,
                lblFechaSalida, dateSalida,
                lblMensaje,
                btnGenerar
        );

        scroll.setContent(formulario);

        VBox root = new VBox();
        root.getChildren().addAll(barraTop, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Sistema TASA — Asignar Despacho");
        stage.setScene(scene);
        stage.centerOnScreen();       // ✅ Centra la ventana en la pantalla
        stage.setResizable(false);    // ✅ Evita que el usuario cambie el tamaño
        stage.show();
    }
}