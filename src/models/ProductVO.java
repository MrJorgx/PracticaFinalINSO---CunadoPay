package models;

public class ProductVO {
    private final int id;
    private final String name;
    private final int stock;
    private final float price;
    private final String URL;


    public ProductVO(int id, String name, int stock, float price, String URL) {
        if(price<0 || stock<0) {
            throw new IllegalArgumentException("El precio o la cantidad no pueden ser menores que 0.");
        }
        
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.URL = URL;
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

    public String getURL() {
        return URL;
    }

    public String toString() {
        return name + "-Stock: " + stock + ", Precio: " + price;
    }
}