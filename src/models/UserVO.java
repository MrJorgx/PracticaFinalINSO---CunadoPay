package models;

public class  UserVO {
    private final int id;
    private final String name;
    private final int tipo;

    public UserVO(int tipo, int id, String name) {
        this.tipo=tipo;
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return this.id;
    }
    public  String getName() {
        return this.name;
    }
    public String toString() {
        if(tipo==1){
            return"JefeVO{" + "id=" + id + ", name=" + name + '}';
        }else{
            return"EmpleadoVO{" + "id=" + id + ", name=" + name + '}';
        }

    }
}