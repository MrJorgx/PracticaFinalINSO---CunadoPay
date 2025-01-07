package models;

public class ProductVO {

    private String name;
    private int stock;
    private float price;
    private String url;
    private int category;

    public ProductVO(String name, int stock, float price, int category, String url) {
        if(price<0 || stock<0) {
            throw new IllegalArgumentException("El precio o la cantidad no pueden ser menores que 0.");
        }
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.category=category;
        this.url = url;
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
    public int getCategory() {
        return category;
    }
    public String getURL() {
        return url;
    }

    //Setters
    public void setName(String name) {
        this.name=name;
    }
    public void setStock(int stock) {
        this.stock=stock;
    }
    public void setPrice(float price) {
        this.price=price;
    }
    public void setCategory(int category) {
        this.category=category;
    }
    public void setURL(String url) {
        this.url=url;
    }

    public String toString() {
        return "Nombre del producto: " +name+ " Stock: "+stock+" Precio: "+price+" Categoria: "+category;
    }
}