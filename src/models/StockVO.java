package models;

public class StockVO {
    private String productName;
    private  int quantity;

    public StockVO(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
    public String getProductName() {
        return this.productName;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity=quantity;
    }
    public void setProductName(String productName) {
        this.productName=productName;
    }
    public String toString(){
        return "Producto: "+productName+ ", cantidad: "+quantity;
    }
}