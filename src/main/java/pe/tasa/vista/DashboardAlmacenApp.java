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

import java.util.List;

/**
 * <h2>DashboardAlmacenApp</h2>
 * Panel del rol ALMACÉN — muestra inventario actual
 * y pedidos listos para preparar/despachar.
 *
 * @author TASA
 * @version 1.0
 */
public class DashboardAlmacenApp {

    private Usuario usuarioActual;

    public DashboardAlmacenApp(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void show(Stage stage) {

        // ── Barra superior ────────────────────────────
        HBox barraTop = new HBox();
        barraTop.setPadding(new Insets(15, 20, 15, 20));
        barraTop.setAlignment(Pos.CENTER_LEFT);
        barraTop.setStyle("-fx-background-color: #00695c;");

        Label lblTitulo = new Label("TASA — Panel de Almacén");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Label lblUsuario = new Label("📦 " + usuarioActual.getNombre() + " (ALMACÉN)");
        lblUsuario.setFont(Font.font("Arial", 13));
        lblUsuario.setTextFill(Color.WHITE);

        Button btnSalir = new Button("Cerrar Sesión");
        btnSalir.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-text-fill: #00695c; " +
                        "-fx-font-size: 12; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;");

        barraTop.getChildren().addAll(lblTitulo, espaciador, lblUsuario,
                new Label("  "), btnSalir);

        // ── Pestañas ────────────────────────────────────
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabInventario = new Tab("📦 Inventario", crearVistaInventario());
        Tab tabPedidos     = new Tab("🚚 Pedidos por Preparar", crearVistaPedidos(stage));

        tabPane.getTabs().addAll(tabInventario, tabPedidos);

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
        root.getChildren().addAll(barraTop, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ✅ PANTALLA CENTRADA Y DE TAMAÑO FIJO (COMO EL LOGIN)
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Sistema TASA — Panel Almacén");
        stage.setScene(scene);
        stage.centerOnScreen();       // ✅ Centra la ventana en la pantalla
        stage.setResizable(false);    // ✅ Evita que el usuario cambie el tamaño
        stage.show();
    }

    // ════════════════════════════════════════════════
    // PESTAÑA 1 — Inventario actual
    // ════════════════════════════════════════════════
    private VBox crearVistaInventario() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label lblTitulo = new Label("Stock Actual de Productos");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<ProductoConStock> tabla = new TableView<>();
        tabla.setStyle("-fx-font-size: 13;");

        TableColumn<ProductoConStock, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().codigo));
        colCodigo.setPrefWidth(100);

        TableColumn<ProductoConStock, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().nombre));
        colNombre.setPrefWidth(250);

        TableColumn<ProductoConStock, String> colUnidad = new TableColumn<>("Unidad");
        colUnidad.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().unidad));
        colUnidad.setPrefWidth(100);

        TableColumn<ProductoConStock, String> colStock = new TableColumn<>("Stock Actual");
        colStock.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().stock)));
        colStock.setPrefWidth(120);

        TableColumn<ProductoConStock, Void> colAccion = new TableColumn<>("Registrar Movimiento");
        colAccion.setPrefWidth(220);
        colAccion.setCellFactory(col -> new TableCell<>() {
            final Button btnEntrada = new Button("+ Entrada");
            final Button btnSalida  = new Button("- Salida");
            final HBox caja = new HBox(5, btnEntrada, btnSalida);
            {
                btnEntrada.setStyle(
                        "-fx-background-color: #2e7d32; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");
                btnSalida.setStyle(
                        "-fx-background-color: #c62828; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-background-radius: 4;");

                btnEntrada.setOnAction(e -> {
                    ProductoConStock p = getTableView().getItems().get(getIndex());
                    registrarMovimiento(p.idProducto, "ENTRADA", tabla);
                });
                btnSalida.setOnAction(e -> {
                    ProductoConStock p = getTableView().getItems().get(getIndex());
                    registrarMovimiento(p.idProducto, "SALIDA", tabla);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : caja);
            }
        });

        tabla.getColumns().addAll(colCodigo, colNombre, colUnidad, colStock, colAccion);

        cargarInventario(tabla);

        box.getChildren().addAll(lblTitulo, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        return box;
    }

    /** Clase auxiliar para mostrar producto + su stock calculado. */
    private static class ProductoConStock {
        int idProducto;
        String codigo, nombre, unidad;
        int stock;
    }

    private void cargarInventario(TableView<ProductoConStock> tabla) {
        tabla.getItems().clear();
        try {
            List<Producto> productos = new ProductoDAO().listarTodos();
            InventarioDAO invDAO = new InventarioDAO();
            for (Producto p : productos) {
                ProductoConStock ps = new ProductoConStock();
                ps.idProducto = p.getIdProducto();
                ps.codigo = p.getCodigo();
                ps.nombre = p.getNombre();
                ps.unidad = p.getUnidadMedida();
                ps.stock = invDAO.calcularStockActual(p.getIdProducto());
                tabla.getItems().add(ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarMovimiento(int idProducto, String tipo, TableView<ProductoConStock> tabla) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Registrar " + tipo);
        dialog.setHeaderText(null);
        dialog.setContentText("Cantidad a registrar (" + tipo + "):");

        dialog.showAndWait().ifPresent(valor -> {
            try {
                int cantidad = Integer.parseInt(valor.trim());
                if (cantidad <= 0) {
                    mostrarAlerta("Error", "La cantidad debe ser mayor a 0.");
                    return;
                }

                Inventario inv = new Inventario();
                inv.setIdProducto(idProducto);
                inv.setIdAlmacen(1); // Almacén Central por defecto
                inv.setTipoMovimiento(tipo);
                inv.setCantidad(cantidad);
                inv.setMotivo("Registrado por " + usuarioActual.getNombre());

                new InventarioDAO().insertar(inv);

                cargarInventario(tabla);
                mostrarAlerta("✔ Movimiento registrado",
                        tipo + " de " + cantidad + " unidades registrada correctamente.");

            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingresa un número válido.");
            } catch (Exception ex) {
                mostrarAlerta("Error", ex.getMessage());
            }
        });
    }

    // ════════════════════════════════════════════════
    // PESTAÑA 2 — Pedidos en despacho (para preparar)
    // ════════════════════════════════════════════════
    private VBox crearVistaPedidos(Stage stage) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label lblTitulo = new Label("Pedidos en Proceso de Despacho");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Pedido> tabla = new TableView<>();
        tabla.setStyle("-fx-font-size: 13;");

        TableColumn<Pedido, String> colId = new TableColumn<>("N° Pedido");
        colId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdPedido())));
        colId.setPrefWidth(90);

        TableColumn<Pedido, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmpresa() != null ?
                                d.getValue().getEmpresa().getRazonSocial() : "-"));
        colEmpresa.setPrefWidth(200);

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getEstado()));
        colEstado.setPrefWidth(140);

        TableColumn<Pedido, String> colTotal = new TableColumn<>("Total S/");
        colTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty("S/ " + d.getValue().getTotal()));
        colTotal.setPrefWidth(110);

        tabla.getColumns().addAll(colId, colEmpresa, colEstado, colTotal);

        try {
            PedidoDAO pedidoDAO = new PedidoDAO();
            List<Pedido> enDespacho = pedidoDAO.listarPorEstado("EN_DESPACHO");
            List<Pedido> confirmados = pedidoDAO.listarPorEstado("CONFIRMADO");
            tabla.getItems().addAll(confirmados);
            tabla.getItems().addAll(enDespacho);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btnActualizar = new Button("🔄 Actualizar lista");
        btnActualizar.setPrefWidth(180); // ✅ Ancho consistente con los demás botones
        btnActualizar.setStyle(
                "-fx-background-color: #00695c; -fx-text-fill: white; " +
                        "-fx-font-size: 13; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 16 8 16;");
        btnActualizar.setOnAction(e -> new DashboardAlmacenApp(usuarioActual).show(stage));

        // ✅ Alinear el botón a la esquina izquierda
        HBox boxBotones = new HBox();
        boxBotones.setAlignment(Pos.CENTER_LEFT);
        boxBotones.getChildren().add(btnActualizar);

        box.getChildren().addAll(lblTitulo, tabla, boxBotones);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        return box;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}