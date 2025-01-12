package controllers;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {

    @FXML
    private TextField  userLog;
    @FXML
    private PasswordField passwordLog;

    private UserDAO userdao= new UserDAO();

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
        if(userdao.userOk(username,password)){
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



}
