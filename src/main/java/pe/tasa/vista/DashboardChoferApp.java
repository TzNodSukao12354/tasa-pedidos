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
import pe.tasa.util.ConexionDB;
import pe.tasa.util.EmailUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>DashboardChoferApp</h2>
 * Panel del Chofer — ve sus guías de despacho, puede
 * aceptarlas, rechazarlas y marcar la entrega final.
 *
 * @author TASA
 * @version 1.0
 */
public class DashboardChoferApp {

    private Usuario usuarioActual;

    public DashboardChoferApp(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    /** Clase interna para representar una fila de la tabla. */
    public static class FilaGuia {
        int idGuia, idPedido;
        String estado, chofer, placa, zona, fechaSalida;
        String correoEmpresa, razonSocial, nombreChofer, telefonoChofer, marcaVehiculo;
        String observacionesPedido;

        public int getIdGuia() { return idGuia; }
        public int getIdPedido() { return idPedido; }
        public String getEstado() { return estado; }
        public String getPlaca() { return placa; }
        public String getZona() { return zona; }
        public String getFechaSalida() { return fechaSalida; }
        public String getRazonSocial() { return razonSocial; }
        public String getObservacionesPedido() { return observacionesPedido; }
    }

    public void show(Stage stage) {

        // ── Barra superior ────────────────────────────
        HBox barraTop = new HBox();
        barraTop.setPadding(new Insets(15, 20, 15, 20));
        barraTop.setAlignment(Pos.CENTER_LEFT);
        barraTop.setStyle("-fx-background-color: #ef6c00;");

        Label lblTitulo = new Label("TASA — Panel del Chofer");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Label lblUsuario = new Label("🚚 " + usuarioActual.getNombre() + " (CHOFER)");
        lblUsuario.setFont(Font.font("Arial", 13));
        lblUsuario.setTextFill(Color.WHITE);

        Button btnSalir = new Button("Cerrar Sesión");
        btnSalir.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-text-fill: #ef6c00; " +
                        "-fx-font-size: 12; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;");

        barraTop.getChildren().addAll(lblTitulo, espaciador, lblUsuario,
                new Label("  "), btnSalir);

        // ── Tabla de guías asignadas ────────────────────
        Label lblGuias = new Label("Mis Guías de Despacho Asignadas");
        lblGuias.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblGuias.setPadding(new Insets(20, 30, 5, 30));

        TableView<FilaGuia> tabla = new TableView<>();
        tabla.setStyle("-fx-font-size: 13;");

        TableColumn<FilaGuia, String> colGuia = new TableColumn<>("N° Guía");
        colGuia.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdGuia())));
        colGuia.setPrefWidth(80);

        TableColumn<FilaGuia, String> colPedido = new TableColumn<>("N° Pedido");
        colPedido.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdPedido())));
        colPedido.setPrefWidth(90);

        TableColumn<FilaGuia, String> colPlaca = new TableColumn<>("Vehículo");
        colPlaca.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getPlaca()));
        colPlaca.setPrefWidth(110);

        TableColumn<FilaGuia, String> colEmpresa = new TableColumn<>("Empresa Cliente");
        colEmpresa.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getRazonSocial()));
        colEmpresa.setPrefWidth(180);

        TableColumn<FilaGuia, String> colZona = new TableColumn<>("Zona Destino");
        colZona.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getZona()));
        colZona.setPrefWidth(150);

        TableColumn<FilaGuia, String> colFecha = new TableColumn<>("Fecha Salida");
        colFecha.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getFechaSalida()));
        colFecha.setPrefWidth(110);

        TableColumn<FilaGuia, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getEstado()));
        colEstado.setPrefWidth(170);

        TableColumn<FilaGuia, Void> colAccion = new TableColumn<>("Acción");
        colAccion.setPrefWidth(280);
        colAccion.setCellFactory(col -> new TableCell<>() {
            final Button btnAceptar   = new Button("Aceptar");
            final Button btnRechazar  = new Button("Rechazar");
            final Button btnEntregado = new Button("✔ Marcar Entregado");
            final HBox caja = new HBox(5, btnAceptar, btnRechazar, btnEntregado);
            {
                btnAceptar.setStyle(
                        "-fx-background-color: #2e7d32; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");
                btnRechazar.setStyle(
                        "-fx-background-color: #c62828; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");
                btnEntregado.setStyle(
                        "-fx-background-color: #1565c0; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");

                btnAceptar.setOnAction(e -> {
                    FilaGuia g = getTableView().getItems().get(getIndex());
                    procesarRespuesta(g, true, stage);
                });

                btnRechazar.setOnAction(e -> {
                    FilaGuia g = getTableView().getItems().get(getIndex());
                    procesarRespuesta(g, false, stage);
                });

                btnEntregado.setOnAction(e -> {
                    FilaGuia g = getTableView().getItems().get(getIndex());
                    registrarEntrega(g, stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    FilaGuia g = getTableView().getItems().get(getIndex());
                    boolean pendiente = g.getEstado().equals("PENDIENTE_ACEPTACION");
                    boolean aceptada  = g.getEstado().equals("ACEPTADA");
                    btnAceptar.setDisable(!pendiente);
                    btnRechazar.setDisable(!pendiente);
                    btnEntregado.setDisable(!aceptada);
                    setGraphic(caja);
                }
            }
        });

        tabla.getColumns().addAll(colGuia, colPedido, colEmpresa, colPlaca, colZona, colFecha, colEstado, colAccion);

        cargarGuias(tabla);

        Label lblDetalle = new Label("Selecciona una guía para ver el destino exacto.");
        lblDetalle.setFont(Font.font("Arial", 13));
        lblDetalle.setWrapText(true);
        lblDetalle.setStyle(
                "-fx-background-color: #fff3e0; " +
                        "-fx-padding: 12; " +
                        "-fx-background-radius: 5;");
        lblDetalle.setMaxWidth(Double.MAX_VALUE);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                lblDetalle.setText("📍 Destino: " +
                        (seleccionado.getObservacionesPedido() != null ?
                                seleccionado.getObservacionesPedido() : "Sin dirección registrada"));
            }
        });

        VBox tablaBox = new VBox(8, lblGuias, tabla, lblDetalle);
        tablaBox.setPadding(new Insets(0, 30, 20, 30));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // ── Acciones ──────────────────────────────────
        btnSalir.setOnAction(e -> {
            try {
                new LoginApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ── Layout principal ──────────────────────────
        VBox root = new VBox();
        root.getChildren().addAll(barraTop, tablaBox);
        VBox.setVgrow(tablaBox, Priority.ALWAYS);

        // ✅ PANTALLA CENTRADA Y DE TAMAÑO FIJO (COMO EL LOGIN)
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Sistema TASA — Panel Chofer");
        stage.setScene(scene);
        stage.centerOnScreen();       // ✅ Centra la ventana en la pantalla
        stage.setResizable(false);    // ✅ Evita que el usuario cambie el tamaño
        stage.show();
    }

    /** Carga las guías asignadas al chofer actual buscándolo por su nombre. */
    private void cargarGuias(TableView<FilaGuia> tabla) {
        List<FilaGuia> lista = new ArrayList<>();
        String sql = """
        SELECT g.idGuia, g.idPedido, g.estado, g.fechaSalida,
               v.placa, v.marca, r.zona,
               c.nombre AS nombreChofer, c.telefono AS telefonoChofer,
               e.correo AS correoEmpresa, e.razonSocial,
               p.observaciones
        FROM GuiaDespacho g
        JOIN Vehiculo v ON g.idVehiculo = v.idVehiculo
        JOIN Ruta r ON g.idRuta = r.idRuta
        JOIN Chofer c ON g.idChofer = c.idChofer
        JOIN Pedido p ON g.idPedido = p.idPedido
        JOIN Empresa e ON p.idEmpresa = e.idEmpresa
        WHERE c.nombre = ?
        ORDER BY g.idGuia DESC
        """;
        try (PreparedStatement ps = ConexionDB.getInstancia()
                .getConexion().prepareStatement(sql)) {
            ps.setString(1, usuarioActual.getNombre());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FilaGuia g = new FilaGuia();
                    g.idGuia = rs.getInt("idGuia");
                    g.idPedido = rs.getInt("idPedido");
                    g.estado = rs.getString("estado");
                    g.placa = rs.getString("placa");
                    g.marcaVehiculo = rs.getString("marca");
                    g.zona = rs.getString("zona");
                    g.fechaSalida = rs.getDate("fechaSalida").toString();
                    g.nombreChofer = rs.getString("nombreChofer");
                    g.telefonoChofer = rs.getString("telefonoChofer");
                    g.correoEmpresa = rs.getString("correoEmpresa");
                    g.razonSocial = rs.getString("razonSocial");
                    g.observacionesPedido = rs.getString("observaciones");
                    lista.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabla.getItems().addAll(lista);
    }

    /** Procesa la aceptación o rechazo de una guía. */
    private void procesarRespuesta(FilaGuia g, boolean aceptado, Stage stage) {
        try {
            String nuevoEstadoGuia = aceptado ? "ACEPTADA" : "RECHAZADA";

            String sqlGuia = "UPDATE GuiaDespacho SET estado=? WHERE idGuia=?";
            try (PreparedStatement ps = ConexionDB.getInstancia()
                    .getConexion().prepareStatement(sqlGuia)) {
                ps.setString(1, nuevoEstadoGuia);
                ps.setInt(2, g.idGuia);
                ps.executeUpdate();
            }

            if (aceptado) {
                String sqlHora = "UPDATE GuiaDespacho SET horaSalidaReal = CURRENT_TIMESTAMP WHERE idGuia = ?";
                try (PreparedStatement ps = ConexionDB.getInstancia()
                        .getConexion().prepareStatement(sqlHora)) {
                    ps.setInt(1, g.idGuia);
                    ps.executeUpdate();
                }

                new PedidoDAO().actualizarEstado(g.idPedido, "EN_DESPACHO");

                EmailUtil.getInstancia().notificarDespacho(
                        g.correoEmpresa,
                        g.razonSocial,
                        g.idPedido,
                        g.nombreChofer,
                        g.telefonoChofer,
                        g.placa,
                        g.marcaVehiculo,
                        g.zona,
                        g.fechaSalida
                );

                new AuditoriaDAO().insertar(new Auditoria(
                        usuarioActual.getIdUsuario(), "GuiaDespacho", "UPDATE",
                        "Chofer " + usuarioActual.getNombre() +
                                " ACEPTÓ la guía #" + g.idGuia
                ));
            } else {
                new AuditoriaDAO().insertar(new Auditoria(
                        usuarioActual.getIdUsuario(), "GuiaDespacho", "UPDATE",
                        "Chofer " + usuarioActual.getNombre() +
                                " RECHAZÓ la guía #" + g.idGuia
                ));
            }

            Alert alert = new Alert(
                    aceptado ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
            alert.setTitle(aceptado ? "Viaje Aceptado" : "Viaje Rechazado");
            alert.setHeaderText(null);
            alert.setContentText(aceptado ?
                    "✔ Aceptaste el viaje #" + g.idGuia + ". Se notificó al cliente." :
                    "✘ Rechazaste el viaje #" + g.idGuia + ". El Admin debe reasignarlo.");
            alert.showAndWait();

            new DashboardChoferApp(usuarioActual).show(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Registra la entrega final del pedido, genera el Ticket y notifica al cliente. */
    private void registrarEntrega(FilaGuia g, Stage stage) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmar Entrega");
        dialog.setHeaderText("Pedido #" + g.idPedido + " — Zona: " + g.zona);
        dialog.setContentText("Nombre de quien recibió el pedido:");

        dialog.showAndWait().ifPresent(nombreReceptor -> {
            if (nombreReceptor.trim().isEmpty()) {
                mostrarAlerta("Error", "Debes ingresar el nombre del receptor.");
                return;
            }

            try {
                // 1. Actualizar estado de la guía a ENTREGADA
                String sqlGuia = "UPDATE GuiaDespacho SET estado='ENTREGADA', fechaEntrega=CURRENT_DATE WHERE idGuia=?";
                try (PreparedStatement ps = ConexionDB.getInstancia()
                        .getConexion().prepareStatement(sqlGuia)) {
                    ps.setInt(1, g.idGuia);
                    ps.executeUpdate();
                }

                // 2. Actualizar el pedido a ENTREGADO
                new PedidoDAO().actualizarEstado(g.idPedido, "ENTREGADO");

                // 3. Generar el Ticket de Entrega
                String sqlTicket = "INSERT INTO TicketEntrega (idGuia, nombreReceptor, estado) " +
                        "VALUES (?, ?, 'CONFIRMADO')";
                try (PreparedStatement ps = ConexionDB.getInstancia()
                        .getConexion().prepareStatement(sqlTicket)) {
                    ps.setInt(1, g.idGuia);
                    ps.setString(2, nombreReceptor.trim());
                    ps.executeUpdate();
                }

                // 4. Auditoría
                new AuditoriaDAO().insertar(new Auditoria(
                        usuarioActual.getIdUsuario(), "GuiaDespacho", "UPDATE",
                        "Chofer " + usuarioActual.getNombre() +
                                " marcó ENTREGADO el pedido #" + g.idPedido +
                                ". Receptor: " + nombreReceptor.trim()
                ));

                // 5. Enviar correo de confirmación al cliente
                EmailUtil.getInstancia().enviar(
                        g.correoEmpresa,
                        "Pedido #" + g.idPedido + " entregado — Sistema TASA",
                        "<html><body style='font-family:Arial;padding:20px'>" +
                                "<h2 style='color:#1565c0'>✔ Pedido Entregado</h2>" +
                                "<p>Estimado <strong>" + g.razonSocial + "</strong>,</p>" +
                                "<p>Su pedido <strong>#" + g.idPedido + "</strong> fue entregado exitosamente.</p>" +
                                "<p><strong>Recibido por:</strong> " + nombreReceptor.trim() + "</p>" +
                                "<p><strong>Zona:</strong> " + g.zona + "</p>" +
                                "<hr/><small>Mensaje automático — Sistema TASA</small>" +
                                "</body></html>"
                );

                mostrarAlerta("✔ Entrega registrada",
                        "Pedido #" + g.idPedido + " marcado como ENTREGADO.\n" +
                                "Se generó el ticket y se notificó al cliente.");

                new DashboardChoferApp(usuarioActual).show(stage);

            } catch (Exception e) {
                mostrarAlerta("Error", e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}