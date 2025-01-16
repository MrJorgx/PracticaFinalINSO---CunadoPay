package controllers;

import models.DAO.*;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.VO.BillVO;
import models.VO.ProductVO;
import models.VO.StockVO;

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
    private BossController bossController     ;
    private ProductController productController;
    private BillController  billController;
    private UserController  userController;
    private TableController tableController;
    private StockController stockController;
    private OrderController orderController;

    private static int numMesa=-1;
    private static double priceIva=0.0;
    private static String fechaFinal="";

    public void initialize(){
        String user= MainController.nameUser();
        bossController = new BossController();
        productController =new ProductController(this);;
        billController= new BillController(this);
        userController= new UserController();
        tableController= new TableController();
        stockController =new StockController(this);
        orderController= new OrderController(this);
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
        productController.loadCategory();
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
        productController.totalPrice();
    }

    public void handlerPay() {
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
            StockVO stock = stockController.change(product);
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
        List<String> errores = stockController.verifyStock(observableStockList);
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
            int idUser= userController.getUserIdByName(userBy.getText());

            if(numMesa==-1){
                BillVO bill=  new BillVO(fecha,aux,0,idUser, tablaPedido);
                orderController.addOrderToTable(0, aux, fecha, 0, idUser);
                int idOrder= orderController.getOrderIdByName(0,aux,fecha,0,idUser);
                for (ProductVO product : productSell) {
                    int id = productController.getProductIdByName(product.getName());
                    if (id != -1) {
                        float totalUni=product.getCat()*product.getPrice();
                        orderController.lineaPedido(id, product.getCat(), product.getPrice(), totalUni, idOrder);
                    }
                }
                orderController.realizarVenta(observableStockList);
                BillVO billVO =new BillVO(fecha,aux,0,idUser, tablaPedido);
                billController.addTicket(fecha, aux, idOrder, idUser);
                billController.generarTicket(bill,idOrder);
                tableController.modifyStateTableToFree(0);
                tablaPedido.getItems().clear();
                total.set(0.00);
                totalNoIVA.set(0.00);
                numMesa=-1;
                fechaFinal="";
                priceIva=0.0;
            }else{
                BillVO bill=  new BillVO(fechaFinal,aux,numMesa,idUser, tablaPedido);

                int idOrder= orderController.getOrderIdByFecha(fechaFinal);
                billController.addTicket(fecha, aux, idOrder, idUser);
                billController.generarTicket(bill,idOrder);
                int idMesa= tableController.getTableIdByNum(numMesa);
                tableController.modifyStateTableToFree(idMesa);
                orderController.modifyStateOrder(fechaFinal);
                tablaPedido.getItems().clear();
                total.set(0.00);
                totalNoIVA.set(0.00);
                fechaFinal="";
                numMesa=-1;
                priceIva=0.0;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta Realizada");
            alert.setHeaderText(null);
            alert.setContentText("La venta se ha realizado con éxito.");
            alert.showAndWait();
        }
    }

    public void handlerAddOrderToTable() throws IOException {
        ObservableList<ProductVO> productSell = tablaPedido.getItems();
        List<StockVO> stockList = new ArrayList<>();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Guardar Pedido en Mesa");
        dialog.setHeaderText("Introduce el número de mesa");
        dialog.setContentText("Mesa:");
        dialog.showAndWait().ifPresent(tableNumberStr -> {
            try {
                int tableNumber = Integer.parseInt(tableNumberStr);
                if (tablaPedido.getItems().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("No hay productos");
                    alert.setContentText("La tabla de pedidos está vacía.");
                    alert.showAndWait();
                } else {
                    for (ProductVO product : productSell) {
                        StockVO stock = stockController.change(product);
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

                    List<String> errores = stockController.verifyStock(observableStockList);
                    if (!errores.isEmpty()) {
                        // Si hay errores, mostrar mensaje
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error de Stock");
                        alert.setHeaderText("No hay suficiente stock para completar la venta");
                        alert.setContentText(String.join("\n", errores));
                        alert.showAndWait();
                    } else {
                        float aux = Float.parseFloat(totalPedido.getText().replace(",", "."));
                        LocalDateTime now = LocalDateTime.now();
                        // Formatear la fecha y hora (opcional)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String fecha = now.format(formatter);
                        if (tableNumber == 0 || tableNumber < 0) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Mesa no disponible");
                            alert.setContentText("La mesa no existe");
                            alert.showAndWait();
                            return;
                        }

                        int idTable = tableController.getTableIdByNum(tableNumber);
                        int idUser = userController.getUserIdByName(userBy.getText());
                        int state = tableController.getState(idTable);
                        if (state != 0) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Mesa ya ocupada");
                            alert.setContentText("La mesa que has intentado guardar esta ocupada intentelo con otra");
                            alert.showAndWait();
                            return;
                        }
                        //Comprobar que la mesa el estado es 0 si no ostias
                        orderController.addOrderToTable(idTable, aux, fecha, 1, idUser);
                        fechaFinal=fecha;
                        int idOrder= orderController.getOrderIdByName(1,aux,fecha,idTable,idUser);
                        for (ProductVO product : productSell) {
                            int id = productController.getProductIdByName(product.getName());
                            if (id != -1) {
                                float totalUni=product.getCat()*product.getPrice();
                                orderController.lineaPedido(id, product.getCat(), product.getPrice(), totalUni, idOrder);
                            }
                        }
                        tablaPedido.getItems().clear();
                        total.set(0.00);
                        totalNoIVA.set(0.00);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Pedido Guardado");
                        alert.setHeaderText(null);
                        alert.setContentText("El pedido se ha guardado en la mesa " + tableNumber + ".");
                        alert.showAndWait();
                    }
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Número de Mesa Inválido");
                alert.setContentText("Por favor, introduce un número válido.");
                alert.showAndWait();
            }
        });
    }


    public void handlerAddOrderToTablePay(ActionEvent event) throws IOException {
        // Crear un diálogo o una ventana emergente
        Stage stage = new Stage();
        stage.setTitle("Mesas ocupadas pendientes de pago");
        // Layout para contener los botones
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);
        // Consulta para obtener las mesas con estado 1
        String sql = "SELECT \"idMesa\", \"numMesa\" FROM \"mesa\" WHERE \"estado\" = 1";

        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idMesa = rs.getInt("idMesa");
                int numeroMesa = rs.getInt("numMesa");

                // Crear un botón para cada mesa
                Button mesaButton = new Button("Mesa: " + numeroMesa);
                mesaButton.setPrefWidth(200);

                // Acción al hacer clic en el botón
                mesaButton.setOnAction(e -> {
                    // Aquí puedes realizar una acción, como abrir detalles de la mesa o realizar un pago
                    //manejar pago
                    tablaPedido.getItems().clear();
                    numMesa=numeroMesa;
                    System.out.println("MEsa:" +numMesa);

                    List<ProductVO> products= productController.recuperar(idMesa);
                    if (products != null && !products.isEmpty()) {
                        ObservableList<ProductVO> observableProducts = FXCollections.observableArrayList(products);
                        // Agregar la lista observable al TableView
                        for(ProductVO product: products){
                            priceIva=priceIva+product.getPre();
                        }
                        tablaPedido.setItems(observableProducts);
                        // Opcional: refrescar la tabla para asegurarse de que los datos se muestren
                        tablaPedido.refresh();
                        total.set(priceIva);
                        totalNoIVA.set(priceIva/1.21);
                        priceIva=0.0;
                    }
                });

                // Añadir el botón al layout
                vbox.getChildren().add(mesaButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar las mesas");
            alert.setContentText("Hubo un problema al consultar las mesas ocupadas.");
            alert.showAndWait();
            return;
        }

        // Configurar y mostrar el diálogo
        Scene scene = new Scene(vbox, 300, 400);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
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
