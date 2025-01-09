package dao;

import models.ProductVO;
import java.util.List;

public abstract class ProductDAO {
    
    //ProductVO getProductById(int id);
    //Product findByURL(String URL);
    //List<ProductVO> getAllProducts();

    abstract void add(ProductVO product);
    abstract void update(ProductVO product);
    abstract void delete(String name);
}