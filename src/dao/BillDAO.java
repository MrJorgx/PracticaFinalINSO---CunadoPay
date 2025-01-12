package dao;

import controllers.DatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.BillVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillDAO {

    public ObservableList<BillVO> getAllBill() {
        ObservableList<BillVO> bill = FXCollections.observableArrayList();
        String sql = "SELECT \"idFactura\" , \"fecha\", \"total\",\"idPedido\",\"idUser\"  FROM \"factura\" ";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idFac= rs.getInt("idFactura");
                String fecha = rs.getString("fecha");
                int idUser = rs.getInt("idUser");
                int idPedido= rs.getInt("idPedido");
                float price= rs.getFloat("total");
                // Crear un nuevo objeto ProductVO y añadirlo a la lista
                BillVO billAdd= new BillVO(idFac, fecha, price, idPedido,idUser);
                bill.add(billAdd);
            }
            return bill;
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public boolean addTicket(String fecha, float price, int idOrder, int idUser){
        String sql = "INSERT INTO \"factura\" ( \"fecha\", \"total\", \"idPedido\", \"idUser\") VALUES (?,?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            stmt.setFloat(2, price);
            stmt.setInt(3, idOrder);
            stmt.setInt(4, idUser);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear el ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void generarTicket(BillVO bill){
        //FEcha, total,idpedido, id user
    }
}
