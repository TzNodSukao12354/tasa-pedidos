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
import pe.tasa.dao.UsuarioDAO;
import pe.tasa.modelo.Empresa;
import pe.tasa.modelo.Usuario;
import pe.tasa.util.EmailUtil;

public class RegistroApp {

    public void show(Stage stage) {

        // ── Panel izquierdo ───────────────────────────
        VBox izquierda = new VBox(20);
        izquierda.setAlignment(Pos.CENTER);
        izquierda.setPadding(new Insets(40));
        izquierda.setStyle("-fx-background-color: #1a237e;");
        izquierda.setPrefWidth(340);

        Label titulo = new Label("TASA");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titulo.setTextFill(Color.WHITE);

        Label subtitulo = new Label("Registro de\nNuevo Usuario");
        subtitulo.setFont(Font.font("Arial", 18));
        subtitulo.setTextFill(Color.LIGHTBLUE);
        subtitulo.setTextAlignment(TextAlignment.CENTER);

        // Botón volver en panel izquierdo
        Button btnVolver = new Button("← Volver al Login");
        btnVolver.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13; " +
                        "-fx-border-color: white; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8 16 8 16;");

        izquierda.getChildren().addAll(titulo, subtitulo, btnVolver);

        // ── Panel derecho — formulario ────────────────
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white;");

        VBox derecha = new VBox(10);
        derecha.setAlignment(Pos.CENTER_LEFT);
        derecha.setPadding(new Insets(30, 40, 30, 40));
        derecha.setStyle("-fx-background-color: #ffffff;");
        derecha.setPrefWidth(420);

        Label lblTitulo = new Label("Crear cuenta");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        lblTitulo.setTextFill(Color.valueOf("#1a237e"));

        // Campos del formulario
        TextField txtNombre    = crearCampo("Nombre completo");
        TextField txtCorreo    = crearCampo("Correo electrónico");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");
        estiloCampo(txtPassword);

        TextField txtTelefono  = crearCampo("Número de teléfono");
        TextField txtRuc       = crearCampo("RUC de la empresa (11 dígitos)");
        TextField txtEmpresa   = crearCampo("Razón social de la empresa");
        TextField txtDireccion = crearCampo("Dirección de la empresa");

        // Checkbox política de privacidad
        CheckBox chkPolitica = new CheckBox("Acepto la Política de Privacidad");
        chkPolitica.setFont(Font.font("Arial", 13));
        chkPolitica.setTextFill(Color.valueOf("#1a237e"));

        // Mensaje
        Label lblMensaje = new Label("");
        lblMensaje.setFont(Font.font("Arial", 12));
        lblMensaje.setWrapText(true);

        // Botón registrar
        Button btnRegistrar = new Button("REGISTRARSE");
        btnRegistrar.setPrefWidth(320);
        btnRegistrar.setPrefHeight(42);
        btnRegistrar.setStyle(
                "-fx-background-color: #1a237e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;");

        // ── Acción registrar ──────────────────────────
        btnRegistrar.setOnAction(e -> {
            String nombre    = txtNombre.getText().trim();
            String correo    = txtCorreo.getText().trim();
            String password  = txtPassword.getText().trim();
            String telefono  = txtTelefono.getText().trim();
            String ruc       = txtRuc.getText().trim();
            String empresa   = txtEmpresa.getText().trim();
            String direccion = txtDireccion.getText().trim();

            // Validaciones
            if (nombre.isEmpty() || correo.isEmpty() ||
                    password.isEmpty() || ruc.isEmpty() || empresa.isEmpty()) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Completa todos los campos obligatorios (*).");
                return;
            }

            if (!chkPolitica.isSelected()) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Debes aceptar la Política de Privacidad.");
                return;
            }

            if (ruc.length() != 11) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ El RUC debe tener exactamente 11 dígitos.");
                return;
            }

            try {
                // 1. Guardar empresa
                EmpresaDAO empresaDAO = new EmpresaDAO();
                Empresa nuevaEmpresa = new Empresa();
                nuevaEmpresa.setRuc(ruc);
                nuevaEmpresa.setRazonSocial(empresa);
                nuevaEmpresa.setTelefono(telefono);
                nuevaEmpresa.setDireccion(direccion);
                nuevaEmpresa.setCorreo(correo);
                nuevaEmpresa.setEstado("ACTIVO");
                empresaDAO.insertar(nuevaEmpresa);

                // 2. Guardar usuario
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setNombre(nombre);
                nuevoUsuario.setCorreo(correo);
                nuevoUsuario.setPassword(password);
                nuevoUsuario.setTelefono(telefono);
                nuevoUsuario.setEstado("ACTIVO");
                nuevoUsuario.setIdRol(2);
                usuarioDAO.insertar(nuevoUsuario);

                // Vincular usuario con su empresa
                try (var ps = pe.tasa.util.ConexionDB.getInstancia()
                        .getConexion()
                        .prepareStatement(
                                "UPDATE Usuario SET idEmpresa=? WHERE correo=?")) {
                    ps.setInt(1, nuevaEmpresa.getIdEmpresa());
                    ps.setString(2, correo);
                    ps.executeUpdate();
                }

                // 3. Enviar correo de bienvenida
                EmailUtil.getInstancia().enviar(
                        correo,
                        "Bienvenido al Sistema TASA",
                        "<html><body style='font-family:Arial;padding:20px'>" +
                                "<h2 style='color:#1a237e'>Bienvenido a TASA</h2>" +
                                "<p>Hola <strong>" + nombre + "</strong>,</p>" +
                                "<p>Tu cuenta ha sido creada exitosamente.</p>" +
                                "<p>Empresa registrada: <strong>" + empresa + "</strong></p>" +
                                "<p>Ya puedes ingresar con tu correo y contraseña.</p>" +
                                "<hr/><small>Sistema TASA</small>" +
                                "</body></html>"
                );

                lblMensaje.setTextFill(Color.GREEN);
                lblMensaje.setText("✔ Registro exitoso. Revisa tu correo.");

                // Limpiar campos
                txtNombre.clear();
                txtCorreo.clear();
                txtPassword.clear();
                txtTelefono.clear();
                txtRuc.clear();
                txtEmpresa.clear();
                txtDireccion.clear();
                chkPolitica.setSelected(false);

            } catch (Exception ex) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("✘ Error: " + ex.getMessage());
            }
        });

        // ── Acción volver ─────────────────────────────
        btnVolver.setOnAction(e -> {
            try {
                new LoginApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        derecha.getChildren().addAll(
                lblTitulo,
                new Label("Nombre *"),    txtNombre,
                new Label("Correo *"),    txtCorreo,
                new Label("Contraseña *"),txtPassword,
                new Label("Teléfono"),    txtTelefono,
                new Label("RUC *"),       txtRuc,
                new Label("Razón Social *"), txtEmpresa,
                new Label("Dirección"),   txtDireccion,
                chkPolitica,
                lblMensaje,
                btnRegistrar
        );

        scroll.setContent(derecha);

        // ── Layout principal ──────────────────────────
        HBox root = new HBox();
        root.getChildren().addAll(izquierda, scroll);

        Scene scene = new Scene(root, 760, 580);
        stage.setTitle("Sistema TASA — Registro");
        stage.setScene(scene);
        stage.show();
    }

    private TextField crearCampo(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        estiloCampo(tf);
        return tf;
    }

    private void estiloCampo(Control campo) {
        campo.setStyle(
                "-fx-padding: 10; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-font-size: 13; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5;");
        ((Region) campo).setPrefHeight(38);
    }
}