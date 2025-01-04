package dao;

import models.ProductVO;

import java.sql.PreparedStatement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Override
public class ProductDAOImpl implements ProductDAO {

    private final Connection connection;

    public ProductDAOImpl(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void add(ProductVO product) {
        String sql = "INSERT INTO products (id, name, stock, price, url) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setInt(3, product.getStock());
            stmt.setFloat(4, product.getPrice());
            stmt.setString(5, product.getURL());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ProductVO product) {
        String sql = "UPDATE products SET name = ?, stock = ?, price = ?, url = ? WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getStock());
            stmt.setFloat(3, product.getPrice());
            stmt.setString(4, product.getURL());
            stmt.setInt(5, product.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProductVO getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return new ProductVO(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"),
                    rs.getString("url")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<ProductVO> getAllProducts() {
        List<ProductVO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try(Statement stmt = connection.createStatement()) {
            ResultsSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                products.add(new ProductVO(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"),
                    rs.getString("url")
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

}
