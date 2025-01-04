package dao;

import model.Product;
import java.util.List;
public class ProductDAO {
    String save(Product product);
    Product findById(int id);
    List<Product> findByName(String name);
    Product findByURL(String URL);
    List<Product> findAll();
    

    void update(Product product,int id, String name, int stock, float price, String URL);
    void delete(int id);
}