package models.DAO;

import controllers.*;
import javafx.collections.ObservableList;
import models.VO.OrderVO;
import models.VO.ProductVO;
import models.VO.StockVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {
    ProductController productController;
    StockController stockController;
    TableController tableController     = new TableController();

    public OrderDAO(BossController boss){
        this.stockController= new StockController(boss);
        this.productController= new ProductController(boss);
    }
    public OrderDAO(InventoryController inventoryController){
        this.stockController= new StockController(inventoryController);
        this.productController= new ProductController(inventoryController);
    }
    public boolean addOrderToTable(int numMesa, float price, String date, int state, int idUser){
        String sql = "INSERT INTO \"pedido\" ( \"precioTotal\", \"fecha\", \"numMesa\", \"estado\", \"idUser\") VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, price);
            stmt.setString(2,date);
            stmt.setInt(3, numMesa);
            stmt.setInt(4, state);
            stmt.setInt(5, idUser);

            int rowsInserted = stmt.executeUpdate();
            tableController.modifyStateTable(numMesa);
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al añadir la venta a la mesa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
            tableController.modifyStateTableToFree(order.getNumberTable());
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear la venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public int getOrderIdByFecha(String fecha){
        String sql =  "SELECT \"idPedido\" FROM \"pedido\" WHERE  \"fecha\" = ? ";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idPedido"); // Devuelve el iduser encontrado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el ID del pedido por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void modifyStateOrder(String fecha){
        String sql = "UPDATE \"pedido\" SET \"estado\" = ? WHERE \"fecha\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 0);
            stmt.setString(2, fecha);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            int id=productController.getProductIdByName(producto.getProductName());
            stockController.actualizarStock(id, producto.getQuantity());
        }
    }
    public boolean lineaPedido(int idProduct, int cantity, float price, float priceTotal, int idOrder){
        String sql = "INSERT INTO \"lineaPedido\" ( \"idProducto\", \"cantidad\", \"precioUnitario\", \"subtotal\", \"idPedido\") VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduct);
            stmt.setInt(2, cantity);
            stmt.setFloat(3, price);
            stmt.setFloat(4,priceTotal);
            stmt.setInt(5,idOrder);

            int rowsInserted = stmt.executeUpdate();

            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear la venta linea: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public ObservableList<ProductVO> tableOrdersgetProductsByTable(ObservableList<ProductVO> tableOrders) {
        return (ObservableList<ProductVO>) tableOrders;
    }
}
