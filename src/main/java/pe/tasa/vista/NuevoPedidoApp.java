package pe.tasa.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import pe.tasa.dao.EmpresaDAO;
import pe.tasa.dao.ProductoDAO;
import pe.tasa.modelo.Empresa;
import pe.tasa.modelo.Producto;
import pe.tasa.modelo.Usuario;
import pe.tasa.servicio.PedidoServicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NuevoPedidoApp {

    private Usuario usuarioActual;

    public NuevoPedidoApp(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void show(Stage stage) {

        // ── Barra superior ────────────────────────────
        HBox barraTop = new HBox();
        barraTop.setPadding(new Insets(15, 20, 15, 20));
        barraTop.setAlignment(Pos.CENTER_LEFT);
        barraTop.setStyle("-fx-background-color: #1a237e;");

        Label lblTitulo = new Label("TASA — Nuevo Pedido");
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
        formulario.setAlignment(Pos.CENTER_LEFT); // ✅ Alineado a la izquierda
        formulario.setPadding(new Insets(25, 30, 25, 30));
        formulario.setStyle("-fx-background-color: #f5f5f5;");

        // ── Empresa automática ────────────────────────
        Label lblEmpresaTitulo = new Label("Empresa Cliente");
        lblEmpresaTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        String nombreEmpresa = "No asignada";
        Empresa empresaUsuario = null;
        try {
            Optional<Empresa> emp = new EmpresaDAO()
                    .buscarPorId(usuarioActual.getIdEmpresa());
            if (emp.isPresent()) {
                empresaUsuario = emp.get();
                nombreEmpresa = empresaUsuario.getRazonSocial() +
                        " — RUC: " + empresaUsuario.getRuc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Label lblEmpresaValor = new Label(nombreEmpresa);
        lblEmpresaValor.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblEmpresaValor.setTextFill(Color.valueOf("#1a237e"));
        lblEmpresaValor.setStyle(
                "-fx-background-color: #e8eaf6; " +
                        "-fx-padding: 12; " +
                        "-fx-background-radius: 5;");
        lblEmpresaValor.setMaxWidth(Double.MAX_VALUE);

        // ── Fecha entrega ─────────────────────────────
        Label lblFecha = new Label("Fecha de Entrega *");
        lblFecha.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        DatePicker dateFecha = new DatePicker(LocalDate.now());
        dateFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        dateFecha.setPrefWidth(400);

        // ── Dirección de entrega ──────────────────────
        Label lblDestino = new Label("Dirección de Entrega *");
        lblDestino.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField txtDestino = crearCampo("Ej: Av. Industrial 123, Chimbote");

        // ── Zona de destino ───────────────────────────
        Label lblZona = new Label("Zona de Destino *");
        lblZona.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<String> cmbZona = new ComboBox<>();
        cmbZona.getItems().addAll(
                "Chimbote", "Nuevo Chimbote", "Trujillo", "Samanco",
                "Huacambo", "Capellanía", "Nepeña", "San Jacinto", "Moro", "Jimbe"
        );
        cmbZona.setPromptText("Seleccione la zona de destino");
        cmbZona.setPrefWidth(400);
        cmbZona.setStyle("-fx-font-size: 13;");

        // ── Observaciones ─────────────────────────────
        Label lblObs = new Label("Observaciones");
        lblObs.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextArea txtObs = new TextArea();
        txtObs.setPromptText("Notas adicionales del pedido...");
        txtObs.setPrefHeight(60);
        txtObs.setStyle("-fx-font-size: 13;");

        // ── Productos ─────────────────────────────────
        Label lblProductos = new Label("Selecciona los Productos *");
        lblProductos.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        lblProductos.setTextFill(Color.valueOf("#1a237e"));

        VBox listaProductos = new VBox(10);
        listaProductos.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15; " +
                        "-fx-background-radius: 8;");

        List<Producto> productos = new ArrayList<>();
        List<HBox> filasProducto = new ArrayList<>();

        try {
            productos = new ProductoDAO().listarTodos();
            for (Producto p : productos) {
                HBox fila = new HBox(15);
                fila.setAlignment(Pos.CENTER_LEFT);
                fila.setPadding(new Insets(5));

                CheckBox chk = new CheckBox();
                chk.setStyle("-fx-font-size: 13;");

                Label lblProd = new Label(p.getNombre());
                lblProd.setPrefWidth(220);
                lblProd.setFont(Font.font("Arial", 13));

                Label lblPrecio = new Label("S/ " + p.getPrecio() + " x " + p.getUnidadMedida());
                lblPrecio.setPrefWidth(160);
                lblPrecio.setTextFill(Color.valueOf("#2e7d32"));
                lblPrecio.setFont(Font.font("Arial", FontWeight.BOLD, 12));

                Label lblCant = new Label("Cant:");
                Spinner<Integer> spinner = new Spinner<>(1, 9999, 1);
                spinner.setPrefWidth(85);
                spinner.setDisable(true);
                spinner.setStyle("-fx-font-size: 13;");

                chk.setOnAction(e -> spinner.setDisable(!chk.isSelected()));

                fila.getChildren().addAll(chk, lblProd, lblPrecio, lblCant, spinner);
                listaProductos.getChildren().add(fila);
                filasProducto.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── Mensaje resultado ─────────────────────────
        Label lblMensaje = new Label("");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lblMensaje.setWrapText(true);

        // ── Botón registrar ───────────────────────────
        Button btnRegistrar = new Button("✔ REGISTRAR PEDIDO");
        btnRegistrar.setPrefWidth(250); // ✅ Ancho consistente y profesional
        btnRegistrar.setPrefHeight(45);
        btnRegistrar.setStyle(
                "-fx-background-color: #2e7d32; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10 20 10 20;");

        // ── Acción registrar ──────────────────────────
        Empresa empresaFinal = empresaUsuario;
        List<Producto> productosFinal = productos;

        btnRegistrar.setOnAction(e -> {

            // Validaciones
            if (empresaFinal == null) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Tu usuario no tiene empresa asignada.");
                return;
            }
            if (dateFecha.getValue() == null) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Selecciona una fecha de entrega.");
                return;
            }
            if (txtDestino.getText().trim().isEmpty()) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Ingresa la dirección de entrega.");
                return;
            }
            if (cmbZona.getValue() == null) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Selecciona la zona de destino.");
                return;
            }

            // Obtener productos seleccionados
            List<int[]> items = new ArrayList<>();
            for (int i = 0; i < filasProducto.size(); i++) {
                HBox fila = filasProducto.get(i);
                CheckBox chk = (CheckBox) fila.getChildren().get(0);
                if (chk.isSelected()) {
                    Spinner<Integer> spinner = (Spinner<Integer>) fila.getChildren().get(4);
                    items.add(new int[]{
                            productosFinal.get(i).getIdProducto(),
                            spinner.getValue()
                    });
                }
            }

            if (items.isEmpty()) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Selecciona al menos un producto.");
                return;
            }

            try {
                // Construir observación con destino
                String observacionCompleta =
                        "Destino: " + txtDestino.getText().trim() +
                                " — Zona: " + cmbZona.getValue() +
                                (txtObs.getText().trim().isEmpty() ? "" :
                                        " — Obs: " + txtObs.getText().trim());

                PedidoServicio servicio = new PedidoServicio();
                servicio.registrarPedido(
                        empresaFinal.getIdEmpresa(),
                        usuarioActual.getIdUsuario(),
                        dateFecha.getValue(),
                        items,
                        observacionCompleta
                );

                lblMensaje.setTextFill(Color.GREEN);
                lblMensaje.setText("✔ Pedido registrado exitosamente. " +
                        "Se envió correo de confirmación a " +
                        empresaFinal.getCorreo());

                // Limpiar formulario
                txtDestino.clear();
                cmbZona.getSelectionModel().clearSelection();
                txtObs.clear();
                for (HBox fila : filasProducto) {
                    CheckBox chk = (CheckBox) fila.getChildren().get(0);
                    chk.setSelected(false);
                    Spinner<Integer> sp = (Spinner<Integer>) fila.getChildren().get(4);
                    sp.setDisable(true);
                    sp.getValueFactory().setValue(1);
                }

            } catch (Exception ex) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Error: " + ex.getMessage());
            }
        });

        // ── Acción volver ─────────────────────────────
        btnVolver.setOnAction(e -> new DashboardApp(usuarioActual).show(stage));

        // ── Agregar al formulario ─────────────────────
        formulario.getChildren().addAll(
                lblEmpresaTitulo, lblEmpresaValor,
                lblFecha,    dateFecha,
                lblDestino,  txtDestino,
                lblZona,     cmbZona,
                lblObs,      txtObs,
                new Separator(),
                lblProductos, listaProductos,
                lblMensaje,
                btnRegistrar
        );

        scroll.setContent(formulario);

        // ── Layout principal ──────────────────────────
        VBox root = new VBox();
        root.getChildren().addAll(barraTop, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // ✅ PANTALLA CENTRADA Y DE TAMAÑO FIJO (COMO EL LOGIN)
        Scene scene = new Scene(root, 900, 600); // Tamaño estándar consistente
        stage.setTitle("Sistema TASA — Nuevo Pedido");
        stage.setScene(scene);
        stage.centerOnScreen();       // ✅ Centra la ventana en la pantalla
        stage.setResizable(false);    // ✅ Evita que el usuario cambie el tamaño
        stage.show();
    }

    private TextField crearCampo(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle(
                "-fx-padding: 10; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-font-size: 13; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5;");
        tf.setPrefHeight(38);
        return tf;
    }
}