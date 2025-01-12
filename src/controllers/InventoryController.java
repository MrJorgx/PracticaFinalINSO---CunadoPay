package controllers;

import dao.*;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.BillVO;
import models.OrderVO;
import models.ProductVO;
import models.StockVO;

public class InventoryController {
    @FXML
    private TextField userBy;
    @FXML
    private GridPane productPane;
    @FXML
    private HBox typeButtons;
    @FXML
    private TableView <ProductVO> tablaPedido;
    @FXML
    private TableColumn<ProductVO, String> columnaArticulo;
    @FXML
    private TableColumn<ProductVO, Integer> columnaCantidad;
    @FXML
    private TableColumn<ProductVO, String> columnaPrecio;
    @FXML
    private TableColumn<ProductVO, Float> columnaTotal;
    @FXML
    private TextField totalPedido;
    @FXML
    private TextField totalPedidoNoIVA;

    private SimpleDoubleProperty total=new SimpleDoubleProperty(0);
    private SimpleDoubleProperty totalNoIVA=new SimpleDoubleProperty(0);


    private BillDAO billdao=new BillDAO();
    private ProductDAO productdao;
    private UserDAO userdao;
    private TableDAO tabledao;
    private StockDAO stockdao;
    private OrderDAO orderdao;

    public void initialize(){
        productdao=new ProductDAO(this);
        userdao=new UserDAO();
        tabledao=new TableDAO();
        stockdao=new StockDAO();
        orderdao= new OrderDAO();
        String user= MainController.nameUser();
        userBy.setText(user);
        userBy.setEditable(false);
        handlerBebidasButton();
        columnaArticulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().catProperty().asObject());
        columnaPrecio.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));
        columnaTotal.setCellValueFactory(cellData -> cellData.getValue().preProperty().asObject());
        totalPedido.textProperty().bind(total.asString("%.2f"));
        totalPedidoNoIVA.textProperty().bind(totalNoIVA.asString("%.2f"));
        tablaPedido.setItems(FXCollections.observableArrayList());
    }


    public void handleBackButton(ActionEvent event) throws IOException {
        Parent homePage = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        // Obtener el escenario actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(homePage);
        // Establecer la nueva escena
        stage.setScene(scene);
        stage.show();
    }

    public void handlerBebidasButton(){
        productdao.loadCategory();
        productPane.setVisible(false);
    }

    public void handlerNewTablePop() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popUppTable.fxml"));
        Parent root = loader.load();

        PopupTableController controller = loader.getController();
        controller.setInventoryController(this);
        controller.actualizar(FXCollections.observableArrayList(tablaPedido.getItems()));

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        tablaPedido.refresh();
        productdao.totalPrice();
    }

    public void handlerPay(){
        ObservableList<ProductVO> productSell = tablaPedido.getItems();
        if(productSell.size()==0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No existen productos ");
            alert.setContentText("No hay ningun producto para realizar la transaccion");
            alert.showAndWait();
            return;
        }
        List<StockVO> stockList = new ArrayList<>();
        for (ProductVO product : productSell) {
            StockVO stock = stockdao.change(product);
            if (stock != null) {
                stockList.add(stock);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo obtener información del stock");
                alert.setContentText("No se pudo convertir el producto: " + product.getName());
                alert.showAndWait();
                return;
            }
        }
        ObservableList<StockVO> observableStockList = FXCollections.observableArrayList(stockList);
        List<String> errores = stockdao.verifyStock(observableStockList);
        if (!errores.isEmpty()) {
            // Si hay errores, mostrar mensaje
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Stock");
            alert.setHeaderText("No hay suficiente stock para completar la venta");
            alert.setContentText(String.join("\n", errores));
            alert.showAndWait();
        } else {
            // Si no hay errores, proceder con la venta
            float aux = Float.parseFloat(totalPedido.getText().replace(",", "."));
            LocalDateTime now = LocalDateTime.now();
            // Formatear la fecha y hora (opcional)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String fecha = now.format(formatter);
            int idUser= userdao.getUserIdByName(userBy.getText());
            //number table =0 si se hace en barra //getnumberMesa() ifmesa 0 esto sino se modifica
            OrderVO order= new OrderVO(2,aux,fecha, 0, idUser);
            BillVO bill=  new BillVO(fecha,aux,0,idUser, tablaPedido);
            orderdao.newOrder(order);
            int idOrder= orderdao.getOrderIdByName(2,aux,fecha,0,idUser);
            orderdao.realizarVenta(observableStockList);
            billdao.addTicket(fecha, aux, idOrder, idUser);
            billdao.generarTicket(bill);
            tablaPedido.getItems().clear();
            total.set(0.00);
            totalNoIVA.set(0.00);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta Realizada");
            alert.setHeaderText(null);
            alert.setContentText("La venta se ha realizado con éxito.");
            alert.showAndWait();
        }
    }

    public SimpleDoubleProperty getTotal(){
        return total;
    }
    public SimpleDoubleProperty getTotalNoIVA(){
        return totalNoIVA;
    }
    public HBox getTypeButtons() {
        return typeButtons;
    }
    public GridPane getProductPane(){
        return productPane;
    }
    public TableView <ProductVO> getTablaPedido(){
        return tablaPedido;
    }
    public void table(){
        tablaPedido.refresh();
    }
    public TableView<ProductVO> getTable() {
        return tablaPedido;
    }











}
