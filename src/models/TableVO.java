package models;
public class TableVO {
    private int id,state, capacity, idOrder;
    //Declaración
    public TableVO(int id, int state, int capacity, int idOrder) {
        this.id = id;
        this.state = state;
        this.capacity = capacity;
        this.idOrder = idOrder;
    }
    //State: 0- disponible 1-reservada 2-ocupada

    //GET
    public int getId() {
        return this.id;
    }
    public int getState() {
        return this.state;
    }
    public int getCapacity() {
        return this.capacity;
    }
    public int getIdOrder() {
        return this.idOrder;
    }
    //SET
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setState(int state) {
        this.state = state;
    }
    public  void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }
    public String toString(){
        return "Numero de mesa: "+id+ ", capacidad: "+capacity+ ", Estado de la mesa: "+state;
    }


}