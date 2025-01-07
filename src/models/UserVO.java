package models;

public class  UserVO {
    private final String name, password;
    private final int tipo;

    public User(int tipo, String name) {
        if(tipo==null||name==null){
            throw new NullPointerException();
        }
        this.tipo=tipo;
        this.name = name;
    }
    public Jefe(int tipo, String name, String password){
        if(tipo==null||name==null||password==null){
            throw new NullPointerException();
        }
        this.tipo=tipo;
        this.name = name;
        this.password=password;
    }

    public  String getName() {
        return this.name;
    }
    public int getTipo() {
        return this.tipo;
    }
    public void setTipo(int tipo) {
        this.tipo=tipo;
    }
    public void setName(String name){
        this.name=name;
    }
    public boolean isJefe() {
        return this.tipo==1;
    }
    public boolean isEmpleado() {
        return this.tipo == 2;
    }
    public void setPassword(String password) {
        if (tipo == 1) {
            this.password = password;
        }
    }
    public String getPassword(){
        if(tipo==1){
            return this.password;
        }
        return "El usuario no es jefe";
    }
    public boolean validarContraseña(String password){
        return this.password.equals(password);
    }

    public String toString() {
        if(tipo==1){
            return"Tipo: Jefe:, Nombre: " + name;
        }else{
            return"Tipo: Empleado:, Nombre: "+ name;
        }
    }
}