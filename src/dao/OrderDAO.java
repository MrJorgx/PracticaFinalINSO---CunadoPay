package dao;

import controllers.DatabaseController;
import javafx.collections.ObservableList;
import models.OrderVO;
import models.StockVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {
    ProductDAO productdao;
    StockDAO stockdao;
    public OrderDAO(){
        this.productdao= new ProductDAO(this);
        this.stockdao=new StockDAO(this);
    }
    public boolean newOrder(OrderVO order){
        String sql = "INSERT INTO \"pedido\" ( \"precioTotal\", \"fecha\", \"numMesa\", \"estado\", \"idUser\") VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, order.getPrice());
            stmt.setString(2,order.getDateString());
            stmt.setInt(3, order.getNumberTable());
            stmt.setInt(4, order.getState());
            stmt.setInt(5, order.getIdWorker());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear la venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public int getOrderIdByName(int estado, float price, String fecha, int numMesa, int idUser){
        String sql =  "SELECT \"idPedido\" FROM \"pedido\" WHERE \"precioTotal\" = ? AND \"fecha\" = ? AND \"numMesa\" = ? AND \"estado\" =? AND \"idUser\" = ?";

        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, price);
            stmt.setString(2, fecha);
            stmt.setInt(3,numMesa);
            stmt.setInt(4,estado);
            stmt.setInt(5,idUser);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idPedido"); // Devuelve el iduser encontrado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el ID del pedido: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    public void realizarVenta(ObservableList<StockVO> productosVenta) {
        for (StockVO producto : productosVenta) {
            int id=productdao.getProductIdByName(producto.getProductName());
            stockdao.actualizarStock(id, producto.getQuantity());
        }
    }
}
