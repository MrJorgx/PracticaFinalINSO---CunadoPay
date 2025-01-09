package models;

public class  UserVO {
    private String name, password;
    private int tipo, id;

    public UserVO(int tipo, String name){
        this.tipo=tipo;
        this.name=name;
    }
    public UserVO(int id, int tipo, String name) {
        this.id=id;
        this.tipo=tipo;
        this.name = name;
    }

    public UserVO(int tipo, String name, String password){
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
    public int getId(){
        return this.id;
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