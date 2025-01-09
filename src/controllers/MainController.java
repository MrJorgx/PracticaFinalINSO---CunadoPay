package controllers;

import database.DatabaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.UserVO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private static String nameUser;
    @FXML
    public void initialize() {
        handlerStart();
    }
    @FXML
    VBox vBox;
    public void goToInventory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acción");
        alert.setHeaderText("Gestión de Inventario");
        alert.showAndWait();
    }

    public void handlerStart(){
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        List<UserVO> user = searchUsers();
        int aux=0;
        if(user!=null&&!user.isEmpty()){
            for(UserVO userVO:user){
                Button empleado = new Button(userVO.getName());
                String color = generateColor(aux);
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
    private String generateColor(int id){
        switch(id%2){
            case 0:
                return "#7BAFD4";
            case 1:
                return "grey";
            default:
                return "blue";
        }
    }

    private List<UserVO> searchUsers(){
        List<UserVO> UserList = new ArrayList<>();
        String sql="SELECT \"idUser\", \"name\", \"password\", \"type\" FROM \"user\"";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Recorrer los resultados de la consulta
            while (rs.next()) {

                String username = rs.getString("name");
                int comprobar =rs.getInt("type");
                //Si el tipo de empleado es 2 es que es empleado y no jefe
                if(comprobar==2){
                    UserVO employee = new UserVO(2, username);
                    UserList.add(employee);
                }


            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return UserList;
    }
}

