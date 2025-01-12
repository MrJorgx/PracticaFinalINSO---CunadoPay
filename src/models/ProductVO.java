package models;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ProductVO {

    private String name,category2;
    private int stock;
    private float price;
    private String url;
    private int category;
    private float aux;
    private SimpleIntegerProperty cat;
    private SimpleFloatProperty pre;
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
    public ProductVO(String name, int cat, float price){
        this.name =  name;
        this.cat= new SimpleIntegerProperty(cat);
        this.price=price;
        this.pre = new SimpleFloatProperty(price);
    }
    public ProductVO(String name, int cat, float price, int category){
        this.name =  name;
        this.cat= new SimpleIntegerProperty(cat);
        this.price=price;
        this.pre = new SimpleFloatProperty(price);
        this.category=category;
    }

    //Categorias 0-bebidas, 1-cafe 2-comidas 3-postres
    public void setCat(int cat) {
        this.cat.set(cat);
    }
    public void setPre(float pre){
        this.pre.set(pre);
    }
    public float getPre(){
        return pre.get();
    }
    public  SimpleIntegerProperty catProperty(){
        return cat;
    }
    public SimpleFloatProperty preProperty(){
        return pre;
    }
    public int getCat(){
        return cat.get();
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
    public float getTotalOne(){
        return getCat()*price;
    }
    public void setTotal(int category, float price){
        this.aux=category*price;
    }


    public String toString() {
        String categoryAux="";
        switch (category) {
            case 0:
                categoryAux = "Comidas";
                break;
            case 1:
                categoryAux = "Cafes";
                break;
            case 2:
                categoryAux = "Postres";
                break;
            case 3:
                categoryAux = "Bebidas";
                break;
            default:
                categoryAux = "Desconocido";
                break;
        }



        return "Nombre del producto: " +name+ " Categoria: "+categoryAux;
    }

}