package models.VO;

import controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableVO {
    private int id,state, capacity, idOrder, numMesa;
    //Declaración
    public TableVO(int id, int state, int capacity, int idOrder, int numMesa) {
        this.id = id;
        this.state = state;
        this.capacity = capacity;
        this.idOrder = idOrder;
        this.numMesa = numMesa;
    }

    public TableVO(int state, int capacity, int numMesa) {
        this.state = state;
        this.capacity = capacity;
        this.numMesa = numMesa;
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
    public int getNumMesa() { return this.numMesa;}
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
    public void setNumMesa(int numMesa) {this.numMesa = numMesa;}
    public String toString() {
        return "Numero de mesa: " + numMesa + ", capacidad: " + capacity + ", Estado de la mesa: " + state;
    }

}