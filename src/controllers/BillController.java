package controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import models.DAO.BillDAO;
import models.VO.BillVO;
import models.VO.ProductVO;

import java.util.ArrayList;
import java.util.List;

public class BillController {
    private BillDAO billDAO;
    private UserController userController = new UserController();
    public BillController(BossController bossController) {
        this.billDAO = new BillDAO(bossController);
    }

    public BillController(InventoryController inventoryController) {
        this.billDAO= new BillDAO(inventoryController);
    }


    public ObservableList<BillVO> getAllBill() {
        return billDAO.getAllBill();
    }

    public boolean addTicket(String fecha, float price, int idOrder, int idUser) {
        return billDAO.addTicket(fecha, price, idOrder, idUser);
    }

    public void generarTicket(BillVO bill, int idOrder) {
        Dialog<String> dialog = new Dialog<String>();
        dialog.setTitle("Factura Generada");
        dialog.setHeaderText("Detalles de la Factura");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        StringBuilder ticketContent = new StringBuilder();
        ticketContent.append("Fecha: ").append(bill.getDateString()).append("\n");
        ticketContent.append("Total: ").append(String.format("%.2f", bill.getPrice())).append(" €\n");

        ticketContent.append("Atendido por: ").append(userController.getUserNameById(bill.getIdWorker())).append("\n");
        ticketContent.append("Productos:\n");
        getProductos(idOrder, ticketContent);

        textArea.setText(ticketContent.toString());
        // Añadir el área de texto al diálogo
        dialog.getDialogPane().setContent(textArea);
        // Añadir un botón de "Aceptar"
        ButtonType acceptButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(acceptButton);
        // Mostrar el diálogo y esperar a que el usuario lo cierre
        dialog.showAndWait();
    }
    public void getProductos(int idOrder, StringBuilder ticketContent) {
        List<ProductVO> bill = billDAO.getProductos(idOrder,ticketContent);
        for(ProductVO product: bill){
            ticketContent.append("- ")
                    .append(product.getName())
                    .append(" x")
                    .append(product.getCat())
                    .append(" (")
                    .append(String.format("%.2f", product.getPre()))
                    .append(" €/u): \n")
            ;
        }

    }

}