package pe.tasa.principal;

import pe.tasa.vista.LoginApp;
import javafx.application.Application;

public class Main extends Application {

    @Override
    public void start(javafx.stage.Stage stage) throws Exception {
        new LoginApp().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}