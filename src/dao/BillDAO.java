package dao;

import controllers.DatabaseController;
import controllers.InventoryController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import models.BillVO;
import models.ProductVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillDAO {

    InventoryController inventoryController =new InventoryController();
    ProductDAO productodao= new ProductDAO(inventoryController);
    UserDAO userDAO= new UserDAO();
    public ObservableList<BillVO> getAllBill() {
        ObservableList<BillVO> bill = FXCollections.observableArrayList();
        String sql = "SELECT \"idFactura\" , \"fecha\", \"total\",\"idPedido\",\"idUser\"  FROM \"factura\"";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idFac= rs.getInt("idFactura");
                String fecha = rs.getString("fecha");
                int idUser = rs.getInt("idUser");
                int idPedido= rs.getInt("idPedido");
                float price= rs.getFloat("total");
                // Crear un nuevo objeto ProductVO y añadirlo a la lista
                BillVO billAdd= new BillVO(idFac, fecha, price, idPedido,idUser);
                bill.add(billAdd);
            }
            return bill;
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public boolean addTicket(String fecha, float price, int idOrder, int idUser){
        String sql = "INSERT INTO \"factura\" ( \"fecha\", \"total\", \"idPedido\", \"idUser\") VALUES (?,?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            stmt.setFloat(2, price);
            stmt.setInt(3, idOrder);
            stmt.setInt(4, idUser);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear el ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void generarTicket(BillVO bill, int idOrder) {
        // Crear un cuadro de diálogo
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Factura Generada");
        dialog.setHeaderText("Detalles de la Factura");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        StringBuilder ticketContent = new StringBuilder();
        ticketContent.append("Fecha: ").append(bill.getDateString()).append("\n");
        ticketContent.append("Total: ").append(String.format("%.2f", bill.getPrice())).append(" €\n");
        ticketContent.append("Atendido por: ").append(userDAO.getUserNameById(bill.getIdWorker())).append("\n");
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

    private void getProductos(int idOrder, StringBuilder ticketContent) {
        // Array para almacenar productos
        String sql = "SELECT \"idProducto\" ,\"cantidad\" ,\"precioUnitario\" FROM \"lineaPedido\" WHERE \"idPedido\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Configurar el parámetro de la consulta
            stmt.setInt(1, idOrder);
            try (ResultSet rs = stmt.executeQuery()) {
                int index = 0; // Índice para llenar el arreglo
                while (rs.next()) {
                    int idProducto = rs.getInt("idProducto");
                    float precio = rs.getFloat("precioUnitario");
                    int cantidad = rs.getInt("cantidad");
                    ticketContent.append("- ")
                            .append(productodao.getProductNameById(idProducto))
                            .append(" x")
                            .append(cantidad)
                            .append(" (")
                            .append(String.format("%.2f", precio))
                            .append(" €/u): \n")
                            ;
                }
            }


        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
