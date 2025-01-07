package models;

import java.util.Date;

public class BillVO {
    private int idBill, idTable, idWorker;
    private Date date;
    private float price;

    public BillVO(int idBill, Date date, float price,int idTable, int idWorker) {
        this.idBill = idBill;
        this.date = date;
        this.price = price;
        this.idTable = idTable;
        this.idWorker = idWorker;
    }
    //GET
    public int getIdBill() {
        return this.idBill;
    }
    public Date getDate() {
        return this.date;
    }
    public  float getPrice() {
        return this.price;
    }
    public int getIdTable() {
        return this.idTable;
    }
    public int getIdWorker() {
        return this.idWorker;
    }
    //SET
    public void setIdBill(int idBill) {
        this.idBill=idBill;
    }
    public void setDate(Date date) {
        this.date=date;
    }
    public void setPrice(float price) {
        this.price=price;
    }
    public void setIdTable(int idTable) {
        this.idTable=idTable;
    }
    public void setIdWorker(int idWorker) {
        this.idWorker=idWorker;
    }
}