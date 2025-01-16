package models.DAO;

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
import models.VO.ProductVO;
import models.VO.TableVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableDAO {



    public void addTable(int numMesa,int capacity, int state) {
        String sql = "INSERT INTO \"mesa\" (\"numMesa\", \"capacidad\", \"estado\") VALUES (?, ?, ?)";

        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numMesa);
            stmt.setInt(2, capacity);
            stmt.setInt(3, state);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al añadir la mesa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int countTables() {
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


    public List<TableVO> modifyTable(){
        List<TableVO> tables = new ArrayList<TableVO>();
        String sql = "SELECT \"numMesa\",\"capacidad\" FROM \"mesa\" WHERE \"numMesa\"!=0";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idTable = rs.getInt("numMesa");
                int quantity = rs.getInt("capacidad");
                TableVO tableadd=new TableVO(0,quantity,idTable);
                tables.add(tableadd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public void modifyStateTable(int num){
        String sql = "UPDATE \"mesa\" SET \"estado\" = ? WHERE \"idMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, num);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void modifyStateTableToFree(int num){
        String sql = "UPDATE \"mesa\" SET \"estado\" = ? WHERE \"idMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 0);
            stmt.setInt(2, num);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public int showState(int state){
        String sql = "SELECT \"numMesa\" FROM \"mesa\"WHERE \"state\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("numMesa");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    //Muestra botones uno para cada mesa
    public  List<Integer> showTableToDelete(){
        List<Integer> table= new ArrayList<>();
        String sql = "SELECT \"numMesa\" FROM \"mesa\" WHERE \"numMesa\"!=0";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idTable = rs.getInt("numMesa");
                table.add(idTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }


    //Elimina la mesa que se ha cliclado
    public void deleteTable(int idTable) {
        String sql = "DELETE FROM \"mesa\" WHERE \"numMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTable);
            stmt.executeUpdate();
            renumberTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Una vez eliminadas las mesas las vuelve a numerar del 1 al 10
    private void renumberTable() {
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


    public int getTableIdByNum(int num) {
        String sql = "SELECT \"idMesa\" FROM \"mesa\" WHERE \"numMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, num);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idMesa");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el ID de la mesa: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }


    public int getState(int id) {
        String sql = "SELECT \"estado\" FROM \"mesa\" WHERE \"idMesa\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("estado");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el estado de la mesa: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }


}
