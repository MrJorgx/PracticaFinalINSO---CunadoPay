package controllers;

import javafx.collections.ObservableList;
import models.DAO.OrderDAO;
import models.VO.OrderVO;
import models.VO.ProductVO;
import models.VO.StockVO;

public class OrderController {
    private OrderDAO orderDAO;

    public OrderController(BossController boss) {
        this.orderDAO = new OrderDAO( boss);
    }
    public OrderController(InventoryController inventoryController){
        this.orderDAO= new OrderDAO(inventoryController);
    }
    public boolean addOrderToTable(int numMesa, float price, String date, int state, int idUser) {
        return orderDAO.addOrderToTable(numMesa, price, date, state, idUser);
    }

    public boolean newOrder(OrderVO order) {
        return orderDAO.newOrder(order);
    }

    public int getOrderIdByFecha(String fecha) {
        return orderDAO.getOrderIdByFecha(fecha);
    }

    public void modifyStateOrder(String fecha) {
        orderDAO.modifyStateOrder(fecha);
    }

    public int getOrderIdByName(int estado, float price, String fecha, int numMesa, int idUser) {
        return orderDAO.getOrderIdByName(estado, price, fecha, numMesa, idUser);
    }

    public void realizarVenta(ObservableList<StockVO> productosVenta) {
        orderDAO.realizarVenta(productosVenta);
    }

    public boolean lineaPedido(int idProduct, int cantity, float price, float priceTotal, int idOrder) {
        return orderDAO.lineaPedido(idProduct, cantity, price, priceTotal, idOrder);
    }

    public ObservableList<ProductVO> tableOrdersgetProductsByTable(ObservableList<ProductVO> tableOrders) {
        return orderDAO.tableOrdersgetProductsByTable(tableOrders);
    }
}