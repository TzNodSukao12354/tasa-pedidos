package pe.tasa.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import pe.tasa.dao.PedidoDAO;
import pe.tasa.modelo.Pedido;
import pe.tasa.modelo.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * <h2>DashboardApp</h2>
 * Panel del Cliente — muestra sus pedidos, permite anularlos
 * y ver el ticket de entrega cuando ya fueron entregados.
 *
 * @author TASA
 * @version 1.0
 */
public class DashboardApp {

    private Usuario usuarioActual;

    public DashboardApp(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void show(Stage stage) {

        // ── Barra superior ────────────────────────────
        HBox barraTop = new HBox();
        barraTop.setPadding(new Insets(15, 20, 15, 20));
        barraTop.setAlignment(Pos.CENTER_LEFT);
        barraTop.setStyle("-fx-background-color: #1a237e;");

        Label lblTitulo = new Label("TASA — Sistema de Gestión de Pedidos");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Label lblUsuario = new Label("👤 " + usuarioActual.getNombre());
        lblUsuario.setFont(Font.font("Arial", 13));
        lblUsuario.setTextFill(Color.LIGHTBLUE);

        Button btnSalir = new Button("Cerrar Sesión");
        btnSalir.setStyle(
                "-fx-background-color: #e53935; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12; " +
                        "-fx-background-radius: 5;");

        barraTop.getChildren().addAll(lblTitulo, espaciador, lblUsuario,
                new Label("  "), btnSalir);

        // ── Tarjetas resumen ──────────────────────────
        HBox tarjetas = new HBox(20);
        tarjetas.setPadding(new Insets(30, 30, 10, 30));
        tarjetas.setAlignment(Pos.CENTER);

        try {
            PedidoDAO pedidoDAO = new PedidoDAO();
            List<Pedido> todos       = pedidoDAO.listarPorUsuario(usuarioActual.getIdUsuario());
            List<Pedido> pendientes  = pedidoDAO.listarPorUsuarioYEstado(usuarioActual.getIdUsuario(), "PENDIENTE");
            List<Pedido> confirmados = pedidoDAO.listarPorUsuarioYEstado(usuarioActual.getIdUsuario(), "CONFIRMADO");

            tarjetas.getChildren().addAll(
                    crearTarjeta("Total Pedidos",
                            String.valueOf(todos.size()),       "#1a237e"),
                    crearTarjeta("Pendientes",
                            String.valueOf(pendientes.size()),  "#f57c00"),
                    crearTarjeta("Confirmados",
                            String.valueOf(confirmados.size()), "#2e7d32")
            );
        } catch (Exception e) {
            tarjetas.getChildren().add(
                    new Label("Error cargando datos: " + e.getMessage())
            );
        }

        // ── Tabla de pedidos ──────────────────────────
        Label lblPedidos = new Label("Últimos Pedidos");
        lblPedidos.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblPedidos.setPadding(new Insets(10, 30, 5, 30));

        TableView<Pedido> tabla = new TableView<>();
        tabla.setStyle("-fx-font-size: 13;");

        TableColumn<Pedido, String> colId = new TableColumn<>("N° Pedido");
        colId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdPedido())));
        colId.setPrefWidth(100);

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEstado()));
        colEstado.setPrefWidth(150);

        TableColumn<Pedido, String> colTotal = new TableColumn<>("Total S/");
        colTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        "S/ " + d.getValue().getTotal()));
        colTotal.setPrefWidth(120);

        TableColumn<Pedido, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getFechaPedido() != null ?
                                d.getValue().getFechaPedido().toLocalDate().toString() : "-"));
        colFecha.setPrefWidth(120);

        // ── Columna de Acción: Anular + Ver Ticket ─────
        TableColumn<Pedido, Void> colAccion = new TableColumn<>("Acción");
        colAccion.setPrefWidth(180);
        colAccion.setCellFactory(col -> new TableCell<>() {
            final Button btnAnular = new Button("Anular");
            final Button btnTicket = new Button("Ver Ticket");
            final HBox caja = new HBox(5, btnAnular, btnTicket);
            {
                btnAnular.setStyle(
                        "-fx-background-color: #e53935; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 11; " +
                                "-fx-background-radius: 4;");
                btnTicket.setStyle(
                        "-fx-background-color: #1565c0; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 11; " +
                                "-fx-background-radius: 4;");

                btnAnular.setOnAction(e -> {
                    Pedido p = getTableView().getItems().get(getIndex());
                    if (p.getEstado().equals("PENDIENTE")) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Anular Pedido");
                        confirm.setHeaderText(null);
                        confirm.setContentText("¿Anular el Pedido #" + p.getIdPedido() + "?");
                        confirm.showAndWait().ifPresent(resp -> {
                            if (resp == ButtonType.OK) {
                                try {
                                    new PedidoDAO().actualizarEstado(
                                            p.getIdPedido(), "ANULADO");
                                    new DashboardApp(usuarioActual).show(stage);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setContentText("Solo se pueden anular pedidos PENDIENTES.");
                        info.showAndWait();
                    }
                });

                btnTicket.setOnAction(e -> {
                    Pedido p = getTableView().getItems().get(getIndex());
                    mostrarTicket(p.getIdPedido());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Pedido p = getTableView().getItems().get(getIndex());
                    btnAnular.setDisable(!p.getEstado().equals("PENDIENTE"));
                    btnTicket.setDisable(!p.getEstado().equals("ENTREGADO"));
                    setGraphic(caja);
                }
            }
        });

        tabla.getColumns().addAll(colId, colEstado, colTotal, colFecha, colAccion);

        try {
            PedidoDAO pedidoDAO = new PedidoDAO();
            tabla.getItems().addAll(pedidoDAO.listarPorUsuario(usuarioActual.getIdUsuario()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox tablaBox = new VBox(5, lblPedidos, tabla);
        tablaBox.setPadding(new Insets(0, 30, 20, 30));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // ── Botones de acción (ALINEADOS A LA ESQUINA IZQUIERDA) ─────────────────────────
        HBox botones = new HBox(15);
        botones.setPadding(new Insets(10, 30, 20, 30));
        botones.setAlignment(Pos.CENTER_LEFT); // ✅ ALINEADO A LA ESQUINA

        Button btnNuevoPedido = crearBoton("➕ Nuevo Pedido", "#1a237e");
        Button btnActualizar  = crearBoton("🔄 Actualizar",   "#37474f");

        botones.getChildren().addAll(btnNuevoPedido, btnActualizar);

        // ── Acciones ──────────────────────────────────
        btnSalir.setOnAction(e -> {
            try {
                new LoginApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnActualizar.setOnAction(e -> {
            try {
                new DashboardApp(usuarioActual).show(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnNuevoPedido.setOnAction(e -> {
            new NuevoPedidoApp(usuarioActual).show(stage);
        });

        // ── Layout principal ──────────────────────────
        VBox root = new VBox();
        root.getChildren().addAll(barraTop, tarjetas, tablaBox, botones);
        VBox.setVgrow(tablaBox, Priority.ALWAYS);

        // ✅ PANTALLA CENTRADA Y DE TAMAÑO FIJO (COMO EL LOGIN)
        Scene scene = new Scene(root, 900, 600); // Tamaño cómodo para la tabla
        stage.setTitle("Sistema TASA — Dashboard");
        stage.setScene(scene);
        stage.centerOnScreen();       // ✅ Centra la ventana en la pantalla
        stage.setResizable(false);    // ✅ Evita que el usuario cambie el tamaño (como el login)
        stage.show();
    }

    // ── Helpers ───────────────────────────────────────

    private VBox crearTarjeta(String titulo, String valor, String color) {
        VBox tarjeta = new VBox(8);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setPrefWidth(200);
        tarjeta.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-background-radius: 10;");

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        lblValor.setTextFill(Color.WHITE);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Arial", 14));
        lblTitulo.setTextFill(Color.WHITE);

        tarjeta.getChildren().addAll(lblValor, lblTitulo);
        return tarjeta;
    }

    private Button crearBoton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setPrefWidth(180); // ✅ Ancho uniforme
        btn.setPrefHeight(40);
        btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8 15 8 15;");
        return btn;
    }

    /** Muestra el ticket de entrega del pedido en una ventana. */
    private void mostrarTicket(int idPedido) {
        try {
            String sql = """
                SELECT t.idTicket, t.nombreReceptor, t.fechaGeneracion, t.estado,
                       g.idGuia, g.fechaSalida, g.fechaEntrega,
                       c.nombre AS nombreChofer, v.placa, v.marca,
                       r.zona
                FROM TicketEntrega t
                JOIN GuiaDespacho g ON t.idGuia = g.idGuia
                JOIN Chofer c ON g.idChofer = c.idChofer
                JOIN Vehiculo v ON g.idVehiculo = v.idVehiculo
                JOIN Ruta r ON g.idRuta = r.idRuta
                WHERE g.idPedido = ?
                """;
            try (PreparedStatement ps = pe.tasa.util.ConexionDB.getInstancia()
                    .getConexion().prepareStatement(sql)) {
                ps.setInt(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String mensaje = String.format(
                                "TICKET DE ENTREGA #%d%n%n" +
                                        "Pedido: #%d%n" +
                                        "Guía de Despacho: #%d%n" +
                                        "Chofer: %s%n" +
                                        "Vehículo: %s (%s)%n" +
                                        "Zona: %s%n" +
                                        "Fecha Salida: %s%n" +
                                        "Fecha Entrega: %s%n" +
                                        "Recibido por: %s%n" +
                                        "Estado: %s",
                                rs.getInt("idTicket"), idPedido, rs.getInt("idGuia"),
                                rs.getString("nombreChofer"), rs.getString("placa"),
                                rs.getString("marca"), rs.getString("zona"),
                                rs.getDate("fechaSalida"), rs.getDate("fechaEntrega"),
                                rs.getString("nombreReceptor"), rs.getString("estado")
                        );

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Ticket de Entrega");
                        alert.setHeaderText("Comprobante de Entrega — Pedido #" + idPedido);
                        alert.setContentText(mensaje);
                        alert.getDialogPane().setMinWidth(450);
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("No se encontró el ticket para este pedido.");
                        alert.showAndWait();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}