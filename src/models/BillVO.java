package models;

import controllers.InventoryController;
import controllers.LoginController;
import dao.UserDAO;
import javafx.scene.control.TableView;

import java.util.Date;

public class BillVO {
    private int idBill, idTable, idWorker, idOrder;
    private Date date;
    private float price;
    private String dateString;
    private TableView <ProductVO> tablaPedido;
    UserDAO userdao= new UserDAO();
    public BillVO(int idBill, Date date, float price,int idTable, int idWorker) {
        this.idBill = idBill;
        this.date = date;
        this.price = price;
        this.idTable = idTable;
        this.idWorker = idWorker;
    }
    public BillVO(String date, float price, int idTable, int idWorker, TableView<ProductVO> tablaPedido) {
        this.dateString = date;
        this.price = price;
        this.idTable = idTable;
        this.idWorker = idWorker;
        this.tablaPedido=tablaPedido;
    }
    public BillVO(int idBill, String date, float price, int idOrder, int idWorker ){
        this.idBill=idBill;
        this.dateString=date;
        this.price=price;
        this.idOrder=idOrder;
        this.idWorker=idWorker;
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
    public String getDateString() {
        return this.dateString;
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

    public String toString() {
        return "Id de la venta: "+idBill+", fecha: "+ dateString+ ", importe: "+ price+ ", trabajador que realizo la venta: "+ userdao.getUserNameById(idWorker);
    }


}