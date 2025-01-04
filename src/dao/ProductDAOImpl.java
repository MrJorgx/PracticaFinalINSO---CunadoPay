package dao;

import models.Product;

public class ProductDAOImpl implements ProductDAO{
    private List<Product> products = new ArrayList<>();

    public String save(Product product) {
        for(Product x : products) {
            if(x.getName().equals(product,name)) {
                return "El producto ya se encuentra en el inventario.";
            }
        }

        products.add(product);
        return "Nuevo producto anadido.";
    }

    public Product findById(int id) {
        for(Product x : products) {
            if(x.getId()==id){
                return x;
            }
        }

        return null;
    }

    

}
