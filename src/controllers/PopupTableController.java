package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.ProductVO;

import java.io.IOException;

public class PopupTableController {
    @FXML
     private TableView<Object> popupTabla;

    @FXML
    private TableColumn<ProductVO, String> nombre;
    @FXML
    private TableColumn<ProductVO, Integer> cantidad;
    @FXML
    private TableColumn<ProductVO, String> precio;
    @FXML
    private TableColumn<ProductVO, Float> total;;
    @FXML
    private TableColumn<ProductVO, Void> acciones;

    private InventoryController inventoryController;

    public void initialize() {
        nombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        cantidad.setCellValueFactory(cellData -> cellData.getValue().catProperty().asObject());
        precio.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));
        total.setCellValueFactory(cellData -> cellData.getValue().preProperty().asObject());
        acciones.setCellFactory(cellData -> new TableCell<ProductVO, Void>() {
            private final Button eliminar= new Button("-");
            {
                eliminar.setOnAction(event -> {
                    ProductVO producto= (ProductVO) getTableView().getItems().get(getIndex());
                    if(producto.getCat()==1){
                        getTableView().getItems().remove(producto);
                        getTableView().refresh();
                        inventoryController.getTable().getItems().remove(producto);
                        inventoryController.table();
                    }else{
                        producto.setCat(producto.getCat()-1);
                        producto.setPre(producto.getPre()- producto.getPrice());
                        getTableView().refresh();
                        inventoryController.table();
                    }
                });
            }
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(eliminar);
                }
            }
        });

        popupTabla.setItems(FXCollections.observableArrayList());
    }
    public void actualizar(ObservableList<Object> data) {
        popupTabla.setItems(data);
    }
    //Actualiza la tabla del pop up con la referencia del controlador de inventory
    public void handleBackButton(ActionEvent event) {

        if (inventoryController != null) {
            inventoryController.table();
        }
        // Cierra el popup
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController=inventoryController;
    }
}
