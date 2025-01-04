package models;

public class Empleado extends User{

    public Empleado(String name,int id) {
        super(2,name, id);
    }
    public String toString(){
        return "Empleado: "+ getName()", ID: "+getId();
    }
}
