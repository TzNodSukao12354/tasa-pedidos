package pe.tasa.vista;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import pe.tasa.dao.UsuarioDAO;
import pe.tasa.modelo.Usuario;

import java.util.Optional;

public class LoginApp extends Application {

    @Override
    public void start(Stage stage) {

        // ── Panel izquierdo — bienvenida ──────────────
        VBox izquierda = new VBox(20);
        izquierda.setAlignment(Pos.CENTER);
        izquierda.setPadding(new Insets(40));
        izquierda.setStyle("-fx-background-color: #1a237e;");
        izquierda.setPrefWidth(380);

        Label titulo = new Label("TASA");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titulo.setTextFill(Color.WHITE);

        Label subtitulo = new Label("Sistema de Gestión\nde Pedidos");
        subtitulo.setFont(Font.font("Arial", 18));
        subtitulo.setTextFill(Color.LIGHTBLUE);
        subtitulo.setTextAlignment(TextAlignment.CENTER);

        Label desc = new Label("Gestiona tus pedidos,\ndespachos y entregas\nde forma eficiente.");
        desc.setFont(Font.font("Arial", 14));
        desc.setTextFill(Color.WHITE);
        desc.setTextAlignment(TextAlignment.CENTER);

        izquierda.getChildren().addAll(titulo, subtitulo, desc);

        // ── Panel derecho — formulario ────────────────
        VBox derecha = new VBox(16);
        derecha.setAlignment(Pos.CENTER);
        derecha.setPadding(new Insets(50));
        derecha.setStyle("-fx-background-color: #ffffff;");
        derecha.setPrefWidth(380);

        Label lblBienvenido = new Label("Bienvenido");
        lblBienvenido.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblBienvenido.setTextFill(Color.valueOf("#1a237e"));

        Label lblSub = new Label("Ingresa tus credenciales");
        lblSub.setFont(Font.font("Arial", 14));
        lblSub.setTextFill(Color.GRAY);

        // Campo correo
        Label lblCorreo = new Label("Correo electrónico");
        lblCorreo.setFont(Font.font("Arial", 13));
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("correo@tasa.pe");
        txtCorreo.setStyle("-fx-padding: 10; -fx-border-radius: 5; " +
                "-fx-border-color: #cccccc; -fx-font-size: 13;");
        txtCorreo.setPrefHeight(40);

        // Campo password
        Label lblPassword = new Label("Contraseña");
        lblPassword.setFont(Font.font("Arial", 13));
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");
        txtPassword.setStyle("-fx-padding: 10; -fx-border-radius: 5; " +
                "-fx-border-color: #cccccc; -fx-font-size: 13;");
        txtPassword.setPrefHeight(40);

        // Mensaje de error
        Label lblError = new Label("");
        lblError.setTextFill(Color.RED);
        lblError.setFont(Font.font("Arial", 12));

        // Botón ingresar
        Button btnIngresar = new Button("INGRESAR");
        btnIngresar.setPrefWidth(280);
        btnIngresar.setPrefHeight(45);
        btnIngresar.setStyle("-fx-background-color: #1a237e; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;");

        // Botón registrarse
        Button btnRegistrar = new Button("REGISTRARSE");
        btnRegistrar.setPrefWidth(280);
        btnRegistrar.setPrefHeight(45);
        btnRegistrar.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #1a237e; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: #1a237e; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;");

        // ── Acción botón ingresar ─────────────────────
        btnIngresar.setOnAction(e -> {
            String correo   = txtCorreo.getText().trim();
            String password = txtPassword.getText().trim();

            if (correo.isEmpty() || password.isEmpty()) {
                lblError.setText("Por favor completa todos los campos.");
                return;
            }

            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Optional<Usuario> usuario = usuarioDAO.buscarPorCorreo(correo);

                if (usuario.isPresent() &&
                        usuario.get().getPassword().equals(password)) {
                    new DashboardApp(usuario.get()).show(stage);
                } else {
                    lblError.setTextFill(Color.RED);
                    lblError.setText("✘ Correo o contraseña incorrectos.");
                }
            } catch (Exception ex) {
                lblError.setTextFill(Color.RED);
                lblError.setText("✘ Error de conexión: " + ex.getMessage());
            }
        });

        // ── Acción botón registrarse ──────────────────
        btnRegistrar.setOnAction(e -> {
            new RegistroApp().show(stage);
        });

        derecha.getChildren().addAll(
                lblBienvenido, lblSub,
                lblCorreo, txtCorreo,
                lblPassword, txtPassword,
                lblError,
                btnIngresar,
                btnRegistrar
        );

        // ── Layout principal ──────────────────────────
        HBox root = new HBox();
        root.getChildren().addAll(izquierda, derecha);

        Scene scene = new Scene(root, 760, 500);
        stage.setTitle("Sistema TASA — Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}