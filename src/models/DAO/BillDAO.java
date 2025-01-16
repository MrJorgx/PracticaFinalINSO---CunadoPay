package models.DAO;

import controllers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import models.VO.BillVO;
import models.VO.ProductVO;
import models.VO.UserVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public BillDAO(BossController boss){
        this.productController = new ProductController(boss);
    }
    public BillDAO(InventoryController inventoryController){
        this.productController = new ProductController(inventoryController);
    }
    public ProductController productController;


    public ObservableList<BillVO> getAllBill() {
        ObservableList<BillVO> bill = FXCollections.observableArrayList();
        String sql = "SELECT \"idFactura\" , \"fecha\", \"total\",\"idPedido\",\"idUser\"  FROM \"factura\"";
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

    public List<ProductVO> getProductos(int idOrder, StringBuilder ticketContent) {
        // Array para almacenar productos
        List<ProductVO> bill = new ArrayList<>();
        String sql = "SELECT \"idProducto\" ,\"cantidad\" ,\"precioUnitario\" FROM \"lineaPedido\" WHERE \"idPedido\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Configurar el parámetro de la consulta
            stmt.setInt(1, idOrder);
            try (ResultSet rs = stmt.executeQuery()) {
                int index = 0; // Índice para llenar el arreglo
                while (rs.next()) {
                    int idProducto = rs.getInt("idProducto");
                    float precio = rs.getFloat("precioUnitario");
                    int cantidad = rs.getInt("cantidad");
                    String name = productController.getProductNameById(idProducto);
                    ProductVO add = new ProductVO(name, cantidad, precio);
                    bill.add(add);
                }
            }


        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
        }
        return bill;
    }

}
