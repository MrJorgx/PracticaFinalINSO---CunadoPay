package controllers;

import dao.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import models.BillVO;
import models.OrderVO;
import models.ProductVO;
import models.StockVO;
import static dao.UserDAO.getNameUserBoss;


public class BossController {
    @FXML
    private TextField userBy;
    @FXML
    private GridPane productPane;
    @FXML
    private HBox typeButtons;
    @FXML
    public TableView <ProductVO> tablaPedido;
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
    private final int MAX_TABLE=10;
    private static int numMesa=-1;
    private static String fechaFinal="";
    private static double priceIva=0.0;
    public void initialize(){
        productdao=new ProductDAO(this);
        userdao=new UserDAO();
        tabledao=new TableDAO();
        stockdao=new StockDAO();
        orderdao= new OrderDAO();
        String user=getNameUserBoss();
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

    public void handlerBebidasButton(){
        productdao.loadCategory();
        productPane.setVisible(false);
    }

    public void handlerNewTablePop() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popUppTableBoss.fxml"));
        Parent root = loader.load();

        PopupTableBossController controller = loader.getController();
        controller.setBossController(this);
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
            if(numMesa==-1){
                BillVO bill=  new BillVO(fecha,aux,0,idUser, tablaPedido);
                orderdao.addOrderToTable(0, aux, fecha, 0, idUser);
                int idOrder= orderdao.getOrderIdByName(0,aux,fecha,0,idUser);
                for (ProductVO product : productSell) {
                    int id = productdao.getProductIdByName(product.getName());
                    if (id != -1) {
                        float totalUni=product.getCat()*product.getPrice();
                        orderdao.lineaPedido(id, product.getCat(), product.getPrice(), totalUni, idOrder);
                    }
                }
                orderdao.realizarVenta(observableStockList);
                billdao.addTicket(fecha, aux, idOrder, idUser);
                billdao.generarTicket(bill,idOrder);
                tabledao.modifyStateTableToFree(0);
                tablaPedido.getItems().clear();
                total.set(0.00);
                totalNoIVA.set(0.00);
                numMesa=-1;
                fechaFinal="";
                priceIva=0.0;
            }else{
                BillVO bill=  new BillVO(fechaFinal,aux,numMesa,idUser, tablaPedido);
                int idOrder= orderdao.getOrderIdByFecha(fechaFinal);
                billdao.addTicket(fecha, aux, idOrder, idUser);
                billdao.generarTicket(bill,idOrder);
                int idMesa= tabledao.getTableIdByNum(numMesa);
                tabledao.modifyStateTableToFree(idMesa);
                orderdao.modifyStateOrder(fechaFinal);
                tablaPedido.getItems().clear();
                total.set(0.00);
                totalNoIVA.set(0.00);
                numMesa=-1;
                fechaFinal="";
                priceIva=0.0;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta Realizada");
            alert.setHeaderText(null);
            alert.setContentText("La venta se ha realizado con éxito.");
            alert.showAndWait();
        }
    }

    public void handlerShowAllProducts()   {
        Dialog<Void> list = new Dialog<>();
        list.setTitle("Lista de Productos");
        list.setHeaderText("Productos  disponibles en la app:");

        list.setResizable(true);

        // Crear la lista de productos
        ListView<ProductVO> showList = new ListView<>();
        ObservableList<ProductVO> allProducts = productdao.showAllProducts();

        if (allProducts != null && !allProducts.isEmpty()) {
            showList.setItems(allProducts);
        } else {
            // Si no hay productos, mostrar un mensaje
            showList.setPlaceholder(new Label("No hay productos disponibles"));
        }
        showList.setPrefWidth(400);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(showList);
        list.getDialogPane().setContent(vbox);

        ButtonType closeButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        list.getDialogPane().getButtonTypes().add(closeButton);
        list.showAndWait();
    }

    public void handleAddProduct( )   {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Añadir Producto");
        dialog.setHeaderText("Introduce los detalles del nuevo producto");

        // Configurar botones
        ButtonType addButtonType = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Crear campos para los datos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textFieldName = new TextField();
        textFieldName.setPromptText("Nombre del producto");

        ChoiceBox<String> choiceBoxOptions = new ChoiceBox<>();
        choiceBoxOptions.getItems().addAll("Bebidas", "Cafes", "Comidas", "Postres");

        TextField textFieldNumber1 = new TextField();
        textFieldNumber1.setPromptText("Precio");

        TextField textFieldNumber2 = new TextField();
        textFieldNumber2.setPromptText("URL");

        // Agregar componentes al grid
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(textFieldName, 1, 0);
        grid.add(new Label("Categoría:"), 0, 1);
        grid.add(choiceBoxOptions, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(textFieldNumber1, 1, 2);
        grid.add(new Label("URL:"), 0, 3);
        grid.add(textFieldNumber2, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convertir los valores de los campos en un objeto
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(textFieldName.getText(), choiceBoxOptions.getValue());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(productDetails -> {
            String name = productDetails.getKey();
            String category = productDetails.getValue();
            String price = textFieldNumber1.getText();
            String url = textFieldNumber2.getText();
            int intro=0;
            if (!name.trim().isEmpty() && !price.trim().isEmpty()) {
                if (!price.matches("\\d+(\\.\\d{1,2})?")) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Introduzca el precio de forma correcta.");
                    error.showAndWait();
                } else {
                    if(productdao.comprobarProduct(name)){
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText(null);
                        error.setContentText("El producto ya se encuentra en la lista");
                        error.showAndWait();
                    }else{
                        if(category.equals("Comidas")){
                            intro=0;
                        }else if(category.equals("Cafes")){
                            intro=1;
                        }else if(category.equals("Postres")){
                            intro=2;
                        }else {
                            intro = 3;
                        }
                        Float auxPrice = Float.parseFloat(price);  // Conversión a float
                        productdao.addProduct(name,intro, auxPrice ,url);
                        int idUser= productdao.getProductIdByName(name);
                        stockdao.addToStock(idUser,auxPrice,50);
                        Alert mostrar = new Alert(Alert.AlertType.INFORMATION);
                        mostrar.setTitle("Correcto");
                        mostrar.setHeaderText(null);
                        mostrar.setContentText("Se ha añadido correctamente el nuevo producto.");
                        mostrar.showAndWait();
                    }
                }
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Todos los campos deben estar completos.");
                error.showAndWait();
            }
        });
    }

    public void handleBackButton(ActionEvent event) throws IOException {
        Parent homePage = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        // Obtener el escenario actual
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(homePage);
        // Establecer la nueva escena
        stage.setScene(scene);
        stage.show();
    }
    //Si datiempo añadirque el jefe se vuelva a loguear para hacer esta accion
    public void handleAddBoss(ActionEvent event) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Añadir Jefe");
        dialog.setHeaderText("Introduce el nombre y la contraseña del nuevo jefe");

        // Configurar botones
        ButtonType addButtonType = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Crear campos para nombre y contraseña
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre usuario");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        // Convertir los valores de los campos en un par de strings
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(nameField.getText(), passwordField.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(credentials -> {
            String name = credentials.getKey();
            String password = credentials.getValue();

            if (!name.trim().isEmpty() && !password.trim().isEmpty()) {
                if (userdao.comprobar(name)) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Ya existe un usuario en la base de datos con ese nombre");
                    error.showAndWait();
                } else {
                    userdao.createBoss(name, password, 1);
                    Alert mostrar = new Alert(Alert.AlertType.INFORMATION);
                    mostrar.setTitle("Correcto");
                    mostrar.setHeaderText(null);
                    mostrar.setContentText("Se ha añadido correctamente el nuevo jefe");
                    mostrar.showAndWait();
                }
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("El nombre y la contraseña no pueden estar vacíos");
                error.showAndWait();
            }
        });
    }
    //Gestionar mesas
    public void handlerAddtable( ){
        Alert table= new Alert(Alert.AlertType.NONE);
        Alert errorAlert= new Alert(Alert.AlertType.ERROR);
        table.setTitle("Gestionar mesas");
        table.setHeaderText("Selecciona una opcion");
        table.setContentText("¿Que acción va a realizar?");
        ButtonType addButtonTable = new ButtonType("Añadir mesa");
        ButtonType manageButtonTable= new ButtonType("Gestionar mesas");
        ButtonType exitButton= new ButtonType("Cancelar");
        table.getButtonTypes().setAll(addButtonTable,manageButtonTable,exitButton);

        table.showAndWait().ifPresent(action -> {
            if (action == addButtonTable) {
                System.out.println("Opción seleccionada: Añadir mesa");
                int actualCapacity= tabledao.countTables();
                //La barra (mesa0 )no cuenta
                actualCapacity=actualCapacity-1;
                if(actualCapacity<MAX_TABLE){
                    tabledao.addTable(actualCapacity+1);
                }else{
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Error al añadir la mesa");
                    errorAlert.setContentText("Numero maximo de mesas alcanzado, no hay mas sitio fisico en el establecimiento");
                    errorAlert.showAndWait();
                }
            } else if (action == manageButtonTable) {
                System.out.println("Opción seleccionada: Gestionar mesas");
                tabledao.manageTables();
            } else {
                System.out.println("Opción seleccionada: Cancelar");
                // Opcional: lógica para cancelar
            }
        });
    }

    public void handleAddWorker( )   {
        TextInputDialog add= new TextInputDialog();
        add.setHeaderText("Añadir empleado");
        add.setContentText("Añadir un nuevo empleado");
        add.setContentText("Introduce el nombre del empleado:");
        Optional<String> result = add.showAndWait();
        result.ifPresent(name-> {
            if(!name.trim().isEmpty()){
                if(userdao.comprobar(name)){
                    Alert error  = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Ya existe el empleado con ese nombre");
                    error.showAndWait();
                }else {
                    userdao.createUser(name, null);
                    Alert mostrar = new Alert(Alert.AlertType.INFORMATION);
                    mostrar.setTitle("Correcto");
                    mostrar.setHeaderText(null);
                    mostrar.setContentText("Se ha añadido correctamente el usuario");
                    mostrar.showAndWait();
                }
            }else{
                Alert error  = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("El nombre del  empleado no puede ser vacio");
                error.showAndWait();
            }
        });
    }
    //Boton de consultar stock
    public void handlerCheckSale(){
        Dialog<Void> list = new Dialog<>();
        list.setTitle("Historial de Ventas");
        list.setHeaderText("Listado de ventas realizadas");
        list.setResizable(true);
        List<BillVO> all = billdao.getAllBill();
        ListView<BillVO> showList = new ListView<>();
        ObservableList<BillVO> observableList = FXCollections.observableArrayList(all);
        if (observableList != null && !observableList.isEmpty()) {
            showList.setItems(observableList);
        } else {
            showList.setPlaceholder(new Label("No hay facturas disponibles"));
        }
        showList.setCellFactory(lv -> new ListCell<BillVO>() {
            @Override
            protected void updateItem(BillVO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        showList.setPrefWidth(400);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(showList);
        list.getDialogPane().setContent(vbox);
        ButtonType closeButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        list.getDialogPane().getButtonTypes().add(closeButton);
        list.showAndWait();
    }

    public void handleCheckStock(){
        Dialog<Void> list = new Dialog<>();
        list.setTitle("Stock en el inventario");
        list.setHeaderText("Stock disponible");
        list.setResizable(true);
        ListView<StockVO> showList = new ListView<>();
        ObservableList<StockVO> allProducts = stockdao.showAllStock();

        if (allProducts != null && !allProducts.isEmpty()) {
            showList.setItems(allProducts);
        } else {
            // Si no hay productos, mostrar un mensaje
            showList.setPlaceholder(new Label("No hay productos disponibles"));
        }
        showList.setCellFactory(lv -> new ListCell<StockVO>() {
            @Override
            protected void updateItem(StockVO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString()); // Asegúrate de que `StockVO` tenga un `toString` apropiado
                    if (item.getQuantity() < 50) {
                        setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    } else if (item.getQuantity() >= 50&& item.getQuantity()< 70) {
                        setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    } else {
                        setStyle(""); // Sin estilo para valores mayores a 50
                    }
                }
            }
        });
        showList.setPrefWidth(400);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(showList);
        list.getDialogPane().setContent(vbox);

        ButtonType closeButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        list.getDialogPane().getButtonTypes().add(closeButton);
        list.showAndWait();
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

                        int idTable = tabledao.getTableIdByNum(tableNumber);
                        int idUser = userdao.getUserIdByName(userBy.getText());
                        int state = tabledao.getState(idTable);
                        if (state != 0) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Mesa ya ocupada");
                            alert.setContentText("La mesa que has intentado guardar esta ocupada intentelo con otra");
                            alert.showAndWait();
                            return;
                        }
                        //Comprobar que la mesa el estado es 0 si no ostias
                        orderdao.addOrderToTable(idTable, aux, fecha, 1, idUser);

                        int idOrder= orderdao.getOrderIdByName(1,aux,fecha,idTable,idUser);
                        for (ProductVO product : productSell) {
                            int id = productdao.getProductIdByName(product.getName());
                            if (id != -1) {
                                float totalUni=product.getCat()*product.getPrice();
                                orderdao.lineaPedido(id, product.getCat(), product.getPrice(), totalUni, idOrder);
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
                    System.out.println();
                    List<ProductVO> products= recuperar(idMesa);
                    if (products != null && !products.isEmpty()) {
                        ObservableList<ProductVO> observableProducts = FXCollections.observableArrayList(products);
                        // Agregar la lista observable al TableView
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

    private List<ProductVO> recuperar(int idMesa) {
        String sql = "SELECT lp.\"idProducto\", lp.\"cantidad\", lp.\"precioUnitario\",  pd.\"idPedido\", pd.\"fecha\" FROM  \"lineaPedido\" lp INNER JOIN \"pedido\" pd ON lp.\"idPedido\" = pd.\"idPedido\" WHERE pd.\"estado\" = 1 AND pd.\"numMesa\" = ?";
        List<ProductVO> productos = new ArrayList<>();
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMesa);
            System.out.println(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Llegue");
                    int cantidad = rs.getInt("cantidad");
                    float precioUnitario = rs.getFloat("precioUnitario");
                    int idProducto = rs.getInt("idProducto");
                    String fecha= rs.getString("fecha");
                    fechaFinal=fecha;
                    float pricef= cantidad*precioUnitario;
                    String name= productdao.getProductNameById(idProducto);
                    // Crear un objeto ProductoVO con los datos obtenidos
                    ProductVO producto = new ProductVO(name, cantidad, precioUnitario, 0);
                    producto.setPre(pricef);
                    productos.add(producto);
                    priceIva= priceIva+ cantidad*precioUnitario;
                }


            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al recuperar productos por mesa: " + e.getMessage(), e);
        }
            return productos;
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
