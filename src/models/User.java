package models;

public class User {
    
    private int tipo;
    private String name;
    private int id;

    public User(int tipo, String name, int id) {
        this.tipo = tipo;
        this.name = name;
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public boolean isJefe() {
        return this.tipo==1;
    }
    public boolean isEmpleado() {
         return this.tipo==2;
    }

}