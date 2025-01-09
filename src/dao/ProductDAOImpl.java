package dao;

import models.ProductVO;

import java.sql.PreparedStatement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductDAOImpl extends ProductDAO {

    private final Connection connection;

    public ProductDAOImpl(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void add(ProductVO product) {
        String sql = "INSERT INTO products (name, stock, price,category, url) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getStock());
            stmt.setFloat(3, product.getPrice());
            stmt.setInt(4, product.getCategory());
            stmt.setString(5, product.getURL());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ProductVO product) {
        String sql = "UPDATE products SET , stock = ?, price = ? category=?, url = ? WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, product.getStock());
            stmt.setFloat(2, product.getPrice());
            stmt.setInt(3, product.getCategory());
            stmt.setString(4, product.getURL());
            stmt.setString(5, product.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM products WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    public ProductVO getProductById(String name) {
        String sql = "SELECT * FROM products WHERE name = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return new ProductVO(

                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"),
                    rs.getInt("category"),
                    rs.getString("url")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ProductVO> getAllProducts() {
        List<ProductVO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try(Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                products.add(new ProductVO(
                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"),
                    rs.getInt("category"),
                    rs.getString("url")
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

}
