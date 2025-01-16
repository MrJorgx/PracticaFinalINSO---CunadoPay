package controllers;

import models.DAO.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.VO.UserVO;

import java.io.IOException;
import java.util.List;

public class MainController {

    private UserController userController;
    private static String nameUser;
    @FXML
    public void initialize() {
         userController= new UserController();
        handlerStart();
    }
    @FXML
    VBox vBox;

    public void handlerStart(){
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        List<UserVO> user = userController.getUsers();
        int aux=0;
        if(user!=null&&!user.isEmpty()){
            for(UserVO userVO:user){
                Button empleado = new Button(userVO.getName());
                String color = userController.generateColor(aux);
                empleado.setStyle("-fx-background-color: " + color + "; -fx-pref-width: 60%;");
                empleado.setMaxWidth(Double.MAX_VALUE);
                root.getChildren().add(empleado);
                empleado.setOnAction(event -> {
                    try {
                        handleMainButton(event, userVO.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                aux++;
            }
        }else {
            // Si no hay empleados, mostrar un mensaje en el VBox
            Label noEmployeesLabel = new Label("No hay empleados disponibles");
            root.getChildren().add(noEmployeesLabel);
        }

        // Añadir el VBox al contenedor principal de la UI
        vBox.getChildren().add(root);
    }
    //Maejadora para que al cliclar en boton jefe te lleve a login del jefe
    public void handleLoggButton(ActionEvent event) throws IOException {
        // Cargar la página de inicio de sesión desde otro archivo FXML
        Parent loginPage = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        // Obtener la ventana actual (stage) y establecer la nueva escena
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loginPage);
        stage.setScene(scene);
        stage.show();
    }
    //Manejador que controlo cuando clickas en usuario para que lleve a inventory
    public void handleMainButton(ActionEvent event,String user) throws IOException {
        nameUser=user;
        Parent stoock = FXMLLoader.load(getClass().getResource("/views/inventory.fxml"));
        // Obtener el escenario actual
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(stoock);
        // Establecer la nueva escena
        stage.setScene(scene);
        stage.show();
    }

    public static String nameUser(){
        return nameUser;
    }

}

