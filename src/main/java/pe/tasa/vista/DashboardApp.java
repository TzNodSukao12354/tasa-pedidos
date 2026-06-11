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

import java.util.List;

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

        TableColumn<Pedido, Void> colAccion = new TableColumn<>("Acción");
        colAccion.setPrefWidth(100);
        colAccion.setCellFactory(col -> new TableCell<>() {
            final Button btnAnular = new Button("Anular");
            {
                btnAnular.setStyle(
                        "-fx-background-color: #e53935; " +
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
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Pedido p = getTableView().getItems().get(getIndex());
                    btnAnular.setDisable(!p.getEstado().equals("PENDIENTE"));
                    setGraphic(btnAnular);
                }
            }
        });

        tabla.getColumns().add(colAccion);

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
        colTotal.setPrefWidth(150);

        TableColumn<Pedido, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getFechaPedido() != null ?
                                d.getValue().getFechaPedido().toLocalDate().toString() : "-"));
        colFecha.setPrefWidth(150);

        tabla.getColumns().addAll(colId, colEstado, colTotal, colFecha);

        try {
            PedidoDAO pedidoDAO = new PedidoDAO();
            tabla.getItems().addAll(pedidoDAO.listarPorUsuario(usuarioActual.getIdUsuario()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox tablaBox = new VBox(5, lblPedidos, tabla);
        tablaBox.setPadding(new Insets(0, 30, 20, 30));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // ── Botones de acción ─────────────────────────
        HBox botones = new HBox(15);
        botones.setPadding(new Insets(10, 30, 20, 30));
        botones.setAlignment(Pos.CENTER_LEFT);

        Button btnNuevoPedido = crearBoton(
                "➕ Nuevo Pedido", "#1a237e");
        Button btnActualizar  = crearBoton(
                "🔄 Actualizar",   "#37474f");

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

        Scene scene = new Scene(root, 760, 580);
        stage.setTitle("Sistema TASA — Dashboard");
        stage.setScene(scene);
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
        btn.setPrefHeight(40);
        btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8 20 8 20;");
        return btn;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}