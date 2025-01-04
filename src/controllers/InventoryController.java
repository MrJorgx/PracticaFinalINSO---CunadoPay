package controllers;

import models.Product;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    
    private List<Product> productos;

    public InventoryController() {
        productos = new ArrayList<>();
    }

    public void agregarProducto(Product producto) {
        productos.add(producto);
    }

    public List<Product> getProductos() {
        return productos;
    }


    public Product buscarProducto(String nombre) {
        for(Product producto : productos) {
            if(producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }

        return null;
    }

    public void eliminarProducto(String nombre) {
        productos.removeIf(producto -> producto.getNombre().equalsIgnoreCase(nombre));
    }

    public void salir () {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) productos.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
}