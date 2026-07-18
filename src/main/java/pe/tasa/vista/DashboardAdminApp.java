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
import pe.tasa.servicio.PedidoServicio;

import java.util.List;

/**
 * <h2>DashboardAdminApp</h2>
 * Panel de control para el rol ADMIN.
 * Permite ver todos los pedidos, confirmarlos y asignar despacho.
 *
 * @author TASA
 * @version 1.0
 */
public class DashboardAdminApp {

    private Usuario usuarioActual;

    public DashboardAdminApp(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void show(Stage stage) {

        // ── Barra superior ────────────────────────────
        HBox barraTop = new HBox();
        barraTop.setPadding(new Insets(15, 20, 15, 20));
        barraTop.setAlignment(Pos.CENTER_LEFT);
        barraTop.setStyle("-fx-background-color: #b71c1c;");

        Label lblTitulo = new Label("TASA — Panel de Administración");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Label lblUsuario = new Label("👤 " + usuarioActual.getNombre() + " (ADMIN)");
        lblUsuario.setFont(Font.font("Arial", 13));
        lblUsuario.setTextFill(Color.web("#ffcdd2"));

        Button btnSalir = new Button("Cerrar Sesión");
        btnSalir.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-text-fill: #b71c1c; " +
                        "-fx-font-size: 12; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;");

        barraTop.getChildren().addAll(lblTitulo, espaciador, lblUsuario,
                new Label("  "), btnSalir);

        // ── Tarjetas resumen ──────────────────────────
        HBox tarjetas = new HBox(20);
        tarjetas.setPadding(new Insets(30, 30, 10, 30));
        tarjetas.setAlignment(Pos.CENTER);

        try {
            PedidoDAO pedidoDAO = new PedidoDAO();
            List<Pedido> todos       = pedidoDAO.listarTodos();
            List<Pedido> pendientes  = pedidoDAO.listarPorEstado("PENDIENTE");
            List<Pedido> confirmados = pedidoDAO.listarPorEstado("CONFIRMADO");

            tarjetas.getChildren().addAll(
                    crearTarjeta("Total Pedidos", String.valueOf(todos.size()), "#1a237e"),
                    crearTarjeta("Pendientes",    String.valueOf(pendientes.size()), "#f57c00"),
                    crearTarjeta("Confirmados",   String.valueOf(confirmados.size()), "#2e7d32")
            );
        } catch (Exception e) {
            tarjetas.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        // ── Tabla de TODOS los pedidos ─────────────────
        Label lblPedidos = new Label("Todos los Pedidos del Sistema");
        lblPedidos.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblPedidos.setPadding(new Insets(10, 30, 5, 30));

        TableView<Pedido> tabla = new TableView<>();
        tabla.setStyle("-fx-font-size: 13;");

        TableColumn<Pedido, String> colId = new TableColumn<>("N° Pedido");
        colId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdPedido())));
        colId.setPrefWidth(85);

        TableColumn<Pedido, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getUsuario() != null ?
                                d.getValue().getUsuario().getNombre() : "-"));
        colCliente.setPrefWidth(150);

        TableColumn<Pedido, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmpresa() != null ?
                                d.getValue().getEmpresa().getRazonSocial() : "-"));
        colEmpresa.setPrefWidth(200);

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEstado()));
        colEstado.setPrefWidth(120);

        TableColumn<Pedido, String> colTotal = new TableColumn<>("Total S/");
        colTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        "S/ " + d.getValue().getTotal()));
        colTotal.setPrefWidth(110);

        TableColumn<Pedido, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getFechaPedido() != null ?
                                d.getValue().getFechaPedido().toLocalDate().toString() : "-"));
        colFecha.setPrefWidth(110);

        // ── Columna de Acción: Confirmar + Despachar ───
        TableColumn<Pedido, Void> colAccion = new TableColumn<>("Acción");
        colAccion.setPrefWidth(220);
        colAccion.setCellFactory(col -> new TableCell<>() {
            final Button btnConfirmar = new Button("Confirmar");
            final Button btnDespacho  = new Button("Despachar");
            final HBox caja = new HBox(5, btnConfirmar, btnDespacho);
            {
                btnConfirmar.setStyle(
                        "-fx-background-color: #2e7d32; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");
                btnDespacho.setStyle(
                        "-fx-background-color: #1565c0; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");

                btnConfirmar.setOnAction(e -> {
                    Pedido p = getTableView().getItems().get(getIndex());
                    try {
                        new PedidoServicio().cambiarEstado(
                                p.getIdPedido(), "CONFIRMADO",
                                usuarioActual.getIdUsuario());
                        new DashboardAdminApp(usuarioActual).show(stage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                btnDespacho.setOnAction(e -> {
                    Pedido p = getTableView().getItems().get(getIndex());
                    new AsignarDespachoApp(usuarioActual, p).show(stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Pedido p = getTableView().getItems().get(getIndex());
                    btnConfirmar.setDisable(!p.getEstado().equals("PENDIENTE"));
                    btnDespacho.setDisable(!p.getEstado().equals("CONFIRMADO"));
                    setGraphic(caja);
                }
            }
        });

        tabla.getColumns().addAll(colId, colCliente, colEmpresa, colEstado, colTotal, colFecha, colAccion);

        try {
            tabla.getItems().addAll(new PedidoDAO().listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox tablaBox = new VBox(5, lblPedidos, tabla);
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
        root.getChildren().addAll(barraTop, tarjetas, tablaBox);
        VBox.setVgrow(tablaBox, Priority.ALWAYS);

        // ✅ PANTALLA CENTRADA Y DE TAMAÑO FIJO (COMO EL LOGIN)
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Sistema TASA — Panel Admin");
        stage.setScene(scene);
        stage.centerOnScreen();       // ✅ Centra la ventana en la pantalla
        stage.setResizable(false);    // ✅ Evita que el usuario cambie el tamaño
        stage.show();
    }

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
}