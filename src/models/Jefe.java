package models;

public class Jefe extends User{
    private String password;

    public Jefe(String name, int id, String  password) {
        super(1, name, id);
        this.password=password
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getPassword(){
        return this.password;
    }
    public boolean validarContraseña(String password){
        return this.password.equals(password);
    }
    public String toString(){
        return "Jefe: "+ getName()", ID: "+getId();
    }
}
