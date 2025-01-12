package dao;

import controllers.DatabaseController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableDAO {
    public void addTable(int numMesa) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        TextInputDialog add = new TextInputDialog();
        add.setTitle("Añadir mesa");
        add.setHeaderText("Nueva mesa");
        add.setContentText("Introduce la capacidad de la mesa");
        //Permite un campo null o no
        Optional<String> result = add.showAndWait();
        result.ifPresent(capacidad -> {
            try {
                int capacidadMesa = Integer.parseInt(capacidad);
                if (capacidadMesa > 0) {
                    System.out.println("Capacidad de la mesa añadida: " + capacidadMesa);
                    setTable(capacidadMesa,0,numMesa);
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
    public boolean setTable(int capacity, int state, int numMesa) {
        String sql= "INSERT INTO \"mesa\" (\"capacidad\", \"estado\",\"numMesa\") VALUES (?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,capacity);
            stmt.setInt(2,state);
            stmt.setInt(3, numMesa);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e) {
            System.err.println("Error al crear la mesa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public int countTables(){
        String sql = "SELECT COUNT(*) AS numOfMesa FROM \"mesa\"";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("numOfMesa");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar las mesas: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    public void manageTables(){
        Stage manage = new Stage();
        manage.setTitle("Gestionar mesas" );
        VBox show = new VBox(10);
        show.setPadding(new Insets(20));
        show.setAlignment(Pos.CENTER);
        Button deleteTable= new Button("Eliminar mesa");
        Button manageTable= new Button("Gestionar mesas");
        deleteTable.setOnAction(event-> {
            manage.close();
            showTableToDelete();
        });
        manageTable.setOnAction(event-> {
            manage.close();
            modifyTable();
        });
        show.getChildren().addAll(deleteTable, manageTable);
        Scene add= new Scene(show,300,150);
        manage.setScene(add);
        manage.show();
    }
    public void modifyTable(){
        Stage modify= new Stage();
        modify.setTitle("Gestionar mesas");
        VBox show = new VBox(10);
        show.setPadding(new Insets(20));
        show.setAlignment(Pos.CENTER);
        String sql = "SELECT \"numMesa\",\"capacidad\" FROM \"mesa\" WHERE \"numMesa\"!=0";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idTable = rs.getInt("numMesa");
                int quantity=rs.getInt("capacidad");
                Button mesaButton = new Button("Mesa " + idTable);
                mesaButton.setOnAction(event -> {
                    // Mostrar cuadro de texto para nueva capacidad
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(quantity));
                    dialog.setTitle("Modificar Mesa");
                    dialog.setHeaderText("Modificar capacidad de Mesa " + idTable);
                    dialog.setContentText("Nueva capacidad:");
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newCapacity -> {
                        modifyCapacityTable(idTable, Integer.parseInt(newCapacity));
                        modify.close();
                    });
                });
                show.getChildren().add(mesaButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Scene add = new Scene(show, 400, 300);
        modify.setScene(add);
        modify.show();
    }
    public void modifyCapacityTable(int idTable, int capacity){
        String sql = "UPDATE \"mesa\" SET \"capacidad\" = ? WHERE \"numMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, capacity);
            stmt.setInt(2, idTable);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Muestra botones uno para cada mesa
    public void showTableToDelete(){
        Stage delete= new Stage();
        delete.setTitle("Eliminar Mesa");
        VBox show= new VBox(10);
        show.setPadding(new Insets(20));
        show.setAlignment(Pos.CENTER);
        List<Button> tableButtons = new ArrayList<>();
        String sql = "SELECT \"numMesa\" FROM \"mesa\" WHERE \"numMesa\"!=0";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idTable = rs.getInt("numMesa");
                Button tableNumberButton= new Button("Mesa " + idTable);
                tableButtons.add(tableNumberButton);
                tableNumberButton.setOnAction(event -> {
                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,"¿Está seguro de que desea eliminar la Mesa " + idTable + "?", ButtonType.YES, ButtonType.NO);
                    confirmDialog.showAndWait();
                    if (confirmDialog.getResult() == ButtonType.YES) {
                        deleteTable(idTable);
                        delete.close();
                    }
                });
                show.getChildren().add(tableNumberButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(show, 400, 300);
        delete.setScene(scene);
        delete.show();
    }
    //Elimina la mesa que se ha cliclado
    public void deleteTable(int idTable){
        String sql = "DELETE FROM \"mesa\" WHERE \"numMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTable);
            stmt.executeUpdate();
            // Renumerar mesas
            renumberTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Una vez eliminadas las mesas las vuelve a numerar del 1 al 10
    public void renumberTable(){
        String selectSql = "SELECT \"numMesa\" FROM \"mesa\" WHERE \"numMesa\" != 0 ORDER BY \"numMesa\"";
        String updateSql = "UPDATE \"mesa\" SET \"numMesa\" = ? WHERE \"numMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql);
             ResultSet rs = selectStmt.executeQuery()) {
            int newId = 1;
            while (rs.next()) {
                int oldId = rs.getInt("numMesa");
                updateStmt.setInt(1, newId);
                updateStmt.setInt(2, oldId);
                updateStmt.executeUpdate();
                newId++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
