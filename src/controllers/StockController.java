package controllers;

import javafx.collections.ObservableList;
import models.DAO.StockDAO;
import models.VO.ProductVO;
import models.VO.StockVO;

import java.util.List;

public class StockController {
    private StockDAO stockDAO;

    public StockController( BossController boss) {
        this.stockDAO = new StockDAO(boss);
    }
    public StockController(InventoryController inventoryController){
        this.stockDAO= new StockDAO(inventoryController);
    }

    public boolean addToStock(int idProduct, float price, int quantity) {
        return stockDAO.addToStock(idProduct, price, quantity);
    }

    public ObservableList<StockVO> showAllStock() {
        return stockDAO.showAllStock();
    }

    public int getStock(int id) {
        return stockDAO.getStock(id);
    }

    public StockVO change(ProductVO product) {
        return stockDAO.change(product);
    }

    public List<String> verifyStock(ObservableList<StockVO> productFind) {
        return stockDAO.verifyStock(productFind);
    }

    public void actualizarStock(int idProducto, int cantidadVendida) {
        stockDAO.actualizarStock(idProducto, cantidadVendida);
    }
}