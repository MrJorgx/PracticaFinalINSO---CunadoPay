package models;

public class ProductVO {

    private final String name;
    private final int stock;
    private final float price;
    private final String url;
    private final int category;

    public Product(String name, int stock, float price, int category, String url) {
        if(tipo==null||stock==null||price==null||category==null){
            throw new NullPointerException();
        }
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