package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.DAO.TableDAO;
import models.VO.TableVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableController {
    private TableDAO tableDAO;

    public TableController() {
        this.tableDAO = new TableDAO();
    }

    public void addTable(int numMesa) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        TextInputDialog add = new TextInputDialog();
        add.setTitle("Añadir mesa");
        add.setHeaderText("Nueva mesa");
        add.setContentText("Introduce la capacidad de la mesa");
        // Permite un campo null o no
        Optional<String> result = add.showAndWait();
        result.ifPresent(capacidad -> {
            try {
                int capacidadMesa = Integer.parseInt(capacidad);
                if (capacidadMesa > 0) {
                    System.out.println("Capacidad de la mesa añadida: " + capacidadMesa);
                    tableDAO.addTable(numMesa, capacidadMesa, 0);
                } else {
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Entrada inválida");
                    errorAlert.setContentText("La capacidad debe ser mayor que 0.");
                    errorAlert.showAndWait();
                }
            } catch (NumberFormatException e) {
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Entrada inválida");
                errorAlert.setContentText("Por favor, introduce un número válido.");
                errorAlert.showAndWait();
            }
        });
    }

    public void modifyStateTableToFree(int num) {
        tableDAO.modifyStateTableToFree(num);
    }
    public  int showState(int state){
        return tableDAO.showState(state);
    }

    public int getTableIdByNum(int num) {
        return tableDAO.getTableIdByNum(num);
    }

    public int countTables() {
        return tableDAO.countTables();
    }

    public int getState(int id) {
        return tableDAO.getState(id);
    }

    public void manageTables() {
        Stage manage = new Stage();
        manage.setTitle("Gestionar mesas");
        VBox show = new VBox(10);
        show.setPadding(new Insets(20));
        show.setAlignment(Pos.CENTER);
        Button deleteTable = new Button("Eliminar mesa");
        Button manageTable = new Button("Gestionar mesas");
        deleteTable.setOnAction(event -> {
            manage.close();
            showTableToDelete();
        });
        manageTable.setOnAction(event -> {
            manage.close();
            modifyTable();
        });
        show.getChildren().addAll(deleteTable, manageTable);
        Scene add = new Scene(show, 300, 150);
        manage.setScene(add);
        manage.show();
    }

    public void modifyStateTable(int num) {
        tableDAO.modifyStateTable(num);
    }

    public void modifyTable() {
        List<TableVO> modifyTables = tableDAO.modifyTable();
        Stage modify = new Stage();
        modify.setTitle("Gestionar mesas");
        VBox show = new VBox(10);
        show.setPadding(new Insets(20));
        show.setAlignment(Pos.CENTER);
        for (TableVO tableVO : modifyTables) {
            Button mesaButton = new Button("Mesa " + tableVO.getNumMesa());
            mesaButton.setOnAction(event -> {
                // Mostrar cuadro de texto para nueva capacidad
                TextInputDialog dialog = new TextInputDialog(String.valueOf(tableVO.getCapacity()));
                dialog.setTitle("Modificar Mesa");
                dialog.setHeaderText("Modificar capacidad de Mesa " + tableVO.getNumMesa());
                dialog.setContentText("Nueva capacidad:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(newCapacity -> {
                    tableDAO.modifyCapacityTable(tableVO.getNumMesa(), Integer.parseInt(newCapacity));
                    modify.close();
                });
            });
            show.getChildren().add(mesaButton);
        }
        Scene add = new Scene(show, 400, 300);
        modify.setScene(add);
        modify.show();
    }
    public void showTableToDelete(){
        List<Button> tableButtons = new ArrayList<>();
        Stage delete= new Stage();
        delete.setTitle("Eliminar Mesa");
        VBox show= new VBox(10);
        show.setPadding(new Insets(20));
        show.setAlignment(Pos.CENTER);
        List<Integer> numberTable = tableDAO.showTableToDelete();
        for(Integer num : numberTable){
            Button tableNumberButton= new Button("Mesa " + num);
            tableButtons.add(tableNumberButton);
            tableNumberButton.setOnAction(event -> {
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,"¿Está seguro de que desea eliminar la Mesa " + num + "?", ButtonType.YES, ButtonType.NO);
                confirmDialog.showAndWait();
                if (confirmDialog.getResult() == ButtonType.YES) {
                    tableDAO.deleteTable(num);
                    delete.close();
                }
            });
            show.getChildren().add(tableNumberButton);
        }

        Scene scene = new Scene(show, 400, 300);
        delete.setScene(scene);
        delete.show();
    }


}


