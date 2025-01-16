package models.DAO;

import controllers.BossController;
import controllers.InventoryController;
import controllers.DatabaseController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import models.VO.ProductVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {


    public List<ProductVO> loadProducts(int aux){
        List<ProductVO> productList = new ArrayList<>();
        String sql= "SELECT \"nombre\", \"precio\" FROM \"producto\" WHERE \"tipo\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aux);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                ProductVO pvo = new ProductVO();
                pvo.setName(rs.getString("nombre"));
                pvo.setPrice(rs.getFloat("precio"));
                productList.add(pvo);
                System.out.println(pvo.toString());
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
        return productList;
    }
    //Comprobar si  el producto existe
    public boolean comprobarProduct(String name){
        String sql = "SELECT COUNT(*) FROM \"producto\" WHERE \"nombre\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement checkStmt = conn.prepareStatement(sql)) {
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Devuelve true si existe, false si no
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al comprobar el usuario en la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //Añadir el producto
    public boolean addProduct(String name, int type, float price, String url){
        String sql= "INSERT INTO \"producto\" (\"nombre\", \"tipo\", \"precio\", \"url\") VALUES (?,?,?, ?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, type);
            stmt.setFloat(3, price);
            stmt.setString(4, url);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public int getProductIdByName(String username) {
        String sql = "SELECT \"idProducto\" FROM \"producto\" WHERE \"nombre\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idProducto"); // Devuelve el iduser encontrado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el ID del producto: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public String getProductNameById(int search) {
        String sql = "SELECT \"nombre\" FROM \"producto\" WHERE \"idProducto\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, search);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre"); // Devuelve el iduser encontrado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el nombre del producto: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    //Mostrar todos los productos  que existen
    public ObservableList<ProductVO> showAllProducts() {
        ObservableList<ProductVO> productList = FXCollections.observableArrayList();
        String sql = "SELECT \"nombre\", \"tipo\"  FROM \"producto\" ";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("nombre");
                int category = rs.getInt("tipo");
                ProductVO product = new ProductVO(name, 0,0,category, "");
                productList.add(product);
            }
            return productList;
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<ProductVO> recuperar(int idMesa) {
        String sql = "SELECT lp.\"idProducto\", lp.\"cantidad\", lp.\"precioUnitario\",  pd.\"idPedido\", pd.\"fecha\" FROM  \"lineaPedido\" lp INNER JOIN \"pedido\" pd ON lp.\"idPedido\" = pd.\"idPedido\" WHERE pd.\"estado\" = 1 AND pd.\"numMesa\" = ?";
        List<ProductVO> productos = new ArrayList<>();
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMesa);
            System.out.println(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Llegue");
                    int cantidad = rs.getInt("cantidad");
                    float precioUnitario = rs.getFloat("precioUnitario");
                    int idProducto = rs.getInt("idProducto");


                    float pricef= cantidad*precioUnitario;
                    String name= getProductNameById(idProducto);
                    // Crear un objeto ProductoVO con los datos obtenidos
                    ProductVO producto = new ProductVO(name, cantidad, precioUnitario, 0);
                    producto.setPre(pricef);
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al recuperar productos por mesa: " + e.getMessage(), e);
        }
        return productos;
    }

}