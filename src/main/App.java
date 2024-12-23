//Clase para cargar la interfaz

package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("Gestión de Inventarios - CuñadoPay");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
