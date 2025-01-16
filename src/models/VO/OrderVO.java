package models.VO;

import java.util.Date;

public class OrderVO {
    private  int id, numberTable, idWorker, state;
    private  float price;
    private Date date;
    private String dateString;

    //State 0 pagado, 1 pendiente de pago
    public OrderVO(int id, int state, float price, Date date, int numberTable, int idWorker) {
        this.id = id;
        this.state = state;
        this.price = price;
        this.date = date;
        this.numberTable = numberTable;
        this.idWorker = idWorker;
    }
    public OrderVO( int state, float price, String date, int numberTable, int idWorker) {
        this.state = state;
        this.price = price;
        this.dateString = date;
        this.numberTable = numberTable;
        this.idWorker = idWorker;
    }

    //GET
    public int getId() {
        return this.id;
    }
    public int getState() {
        return this.state;
    }
    public float getPrice() {
        return this.price;
    }
    public Date getDate() {
        return this.date;
    }
    public int getNumberTable() {
        return this.numberTable;
    }
    public int getIdWorker() {
        return this.idWorker;
    }
    public String getDateString() {
        return this.dateString;
    }
    //SET
    public void setId(int id) {
        this.id = id;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setNumberTable(int numberTable) {
        this.numberTable = numberTable;
    }
    public void setIdWorker(int idWorker) {
        this.idWorker = idWorker;
    }
}
