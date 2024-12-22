package models;

public class Product {

    private int id;
    private String name;
    private int stock;
    private float price;

    public Product(int id, String name, int stock, float price) {

        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " - Stock: " + stock + ", Precio: " + price;
    }
}