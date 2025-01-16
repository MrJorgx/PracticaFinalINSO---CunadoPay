package models.DAO;

import controllers.BossController;
import controllers.DatabaseController;
import controllers.InventoryController;
import controllers.ProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.VO.ProductVO;
import models.VO.StockVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    private ProductController productController;
    public StockDAO(BossController bc){
        this.productController=new ProductController(bc);
    }
    public StockDAO(InventoryController inventoryController){
        this.productController= new ProductController(inventoryController);
    }
    public boolean addToStock(int idproduct,  float price, int  quantity) {
        String sql = "INSERT INTO \"inventario\" (\"idProducto\", \"cantidadDisponible\", \"precio\") VALUES (?, ?, ?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idproduct);
            stmt.setInt(2, quantity);
            stmt.setFloat(3, price);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar stock al inventario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<StockVO> showAllStock() {
        ObservableList<StockVO> stock = FXCollections.observableArrayList();
        String sql = "SELECT \"idProducto\", \"cantidadDisponible\", \"precio\"  FROM \"inventario\" ";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("idProducto");
                int quantity = rs.getInt("cantidadDisponible");
                float price= rs.getFloat("precio");
                String nombre=productController.getProductNameById(id);
                StockVO add = new StockVO(nombre, quantity,price);
                stock.add(add);
            }
            return stock;
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public int getStock(int id){
        String sql = "SELECT \"cantidadDisponible\" FROM \"inventario\" WHERE \"idProducto\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cantidadDisponible");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public StockVO change(ProductVO product){
        int idPro= productController.getProductIdByName(product.getName());
        if(idPro==-1){
            return null;
        }
        int quantity= getStock(idPro);
        return new StockVO(product.getName(),product.getCat(),product.getPrice());
    }
    public List<String> verifyStock(ObservableList<StockVO> productFind){
        List<String> errores = new ArrayList<>();
        for(StockVO verify : productFind){
            int idProduct= productController.getProductIdByName(verify.getProductName());
            int quantity= getStock(idProduct);
            if(quantity< verify.getQuantity()){
                errores.add("Stock insuficiente para realizar la venta, para el producto: " +verify.getProductName());
            }
        }
        return errores;
    }
    public void actualizarStock(int idProducto, int cantidadVendida) {
        String sql = "UPDATE \"inventario\" SET \"cantidadDisponible\" = \"cantidadDisponible\" - ? WHERE \"idProducto\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidadVendida);
            stmt.setInt(2, idProducto);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("El stock del producto con ID " + idProducto + " se actualizó correctamente. Cantidad vendida: " + cantidadVendida);
            } else {
                System.err.println("No se pudo actualizar el stock. Verifica si el producto con ID " + idProducto + " existe.");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
