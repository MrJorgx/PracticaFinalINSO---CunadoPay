package dao;

import model.ProductVO;
import java.util.List;

public class ProductDAO {
    
    ProductVO getProductById(int id);
    Product findByURL(String URL);
    List<ProductVO> getAllProducts();

    void add(ProductVO product);
    void update(ProductVO product);
    void delete(int id);
}