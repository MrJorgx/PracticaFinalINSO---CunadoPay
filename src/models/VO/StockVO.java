package models.VO;

public class StockVO {
    private String productName;
    private  int quantity;
    private float price;

    public StockVO(String productName, int quantity,float price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price=price;
    }
    public String getProductName() {
        return this.productName;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public float getPrice() {
        return this.price;
    }
    public void setQuantity(int quantity) {
        this.quantity=quantity;
    }
    public void setProductName(String productName) {
        this.productName=productName;
    }
    public void setPrice(float price) {
        this.price=price;
    }
    public String toString(){
        return "Producto: "+productName+ ", cantidad: "+quantity+", precio: "+price;
    }
}