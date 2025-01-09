package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import database.DatabaseController;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {

    @FXML
    private TextField  userLog;
    @FXML
    private PasswordField passwordLog;
    // Manejadora para el botón de login
    @FXML

    public void handleBackButton(ActionEvent event) throws IOException {
        Parent homePage = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        // Obtener el escenario actual
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(homePage);
        // Establecer la nueva escena
        stage.setScene(scene);
        stage.show();
    }

    //Modificar ruta a cosas que solo pueda hacer el jefe
    public void handleLoginButton(ActionEvent event) throws IOException{
        String username= userLog.getText();
        String password= passwordLog.getText();
        if(userOk(username,password)){
            Alert info= new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Inicion de sesion correcto");
            info.setHeaderText(null);
            info.setContentText("Bienvenido de nuevo al sistema jefe: "+username);
            info.showAndWait();

            Parent stoock = FXMLLoader.load(getClass().getResource("/views/boss.fxml"));
            // Obtener el escenario actual
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(stoock);
            // Establecer la nueva escena
            stage.setScene(scene);
            stage.show();

        }else{
            Alert error= new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Usuario o contraseña incorrectos, vuelva a intentarlo");
            error.showAndWait();
        }
    }
    private boolean userOk(String username, String password) {
        String sql = "SELECT * FROM \"user\" WHERE \"name\" = ? AND \"password\" = ?";

        try (Connection conn = DatabaseController.main();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();  // Si encuentra un resultado, la autenticación es exitosa
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
