package models;

public class Product {

    private int id;
    private String name;
    private int stock;
    private float price;
    private String URL;

    public Product(int id, String name, int stock, float price, String URL) {

        if(price<0 || stock<0) {
            throw new IllegalArgumentException("El precio o la cantidad no pueden ser menores que 0.");
        }

        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.URL = URL;
    }

    // GETTERS
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getStock() {
        return this.stock;
    }

    public float getPrice() {
        return this.price;
    }

    public String getURL() {
        return this.URL;
    }


    // SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(float price) {
        this.price = price;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }


    @Override
    public String toString() {
        return name + " - Stock: " + stock + ", Precio: " + price;
    }
}