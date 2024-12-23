package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MainController {

    @FXML
    public void goToInventory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acción");
        alert.setHeaderText("Gestión de Inventario");
        alert.showAndWait();
    }
}
