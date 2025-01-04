package models;

public class Product {

    private int id;
    private String name;
    private int stock;
    private float price;
    private String URL;

    public Product(int id, String name, int stock, float price, String URL) {

        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.URL=  url;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }
    public String getURL() {
            return this.URL;
    }


    @Override
    public String toString() {
        return name + " - Stock: " + stock + ", Precio: " + price;
    }
}