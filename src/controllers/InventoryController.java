package controllers;

//import models.ProductVO;
//import java.util.ArrayList;
//import java.util.List;

import database.DatabaseController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.ProductVO;
import javafx.scene.layout.VBox;
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

    private SimpleDoubleProperty total=new SimpleDoubleProperty(0);

    public void initialize(){
        String user= MainController.nameUser();
        userBy.setText(user);
        userBy.setEditable(false);
       handlerBebidasButton();
        columnaArticulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().catProperty().asObject());
        columnaPrecio.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));
        columnaTotal.setCellValueFactory(cellData -> cellData.getValue().preProperty().asObject());
        totalPedido.textProperty().bind(total.asString("%.2f"));
        tablaPedido.setItems(FXCollections.observableArrayList());
    }
    //Boton de salir
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
        loadCategory();
        productPane.setVisible(false);
    }






    //Botones automaticos de las categorias de los productos de la base de datos
    private void loadCategory(){
        String[] categoryName  ={"Comidas", "Cafes", "Postres", "Bebidas"};
        typeButtons.getChildren().clear();
        for(int i=0; i < categoryName.length; i++){
            int aux=i;
            Button categoryButton= new Button(categoryName[i]);
            categoryButton.setStyle("-fx-min-width: 100px; -fx-background-color: lightgray;");
            categoryButton.setOnAction(event ->{
                    System.out.println("Categoria cliclada: "+ categoryName[aux]);
                    loadProducts(aux);
            });
            typeButtons.getChildren().add(categoryButton);
            categoryButton.setDisable(false);
        }
    }
    //Carga los productos de cada categoria
    private void loadProducts(int aux){
        productPane.getChildren().clear();
        productPane.setVisible(true);
        String sql= "SELECT \"nombre\", \"precio\" FROM \"producto\" WHERE \"tipo\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aux);
            ResultSet rs = stmt.executeQuery();
            int row=0;
            int col=0;
            while(rs.next()){
                String productName=rs.getString("nombre");
                float productPrice=rs.getFloat("precio");
                Button productButton= new Button(productName);
                productButton.setStyle("-fx-min-width: 100px; -fx-min-height: 40px; -fx-background-color: lightblue;");
                productPane.add(productButton, col, row);
                productButton.setOnAction(event -> {
                    addOrder(productName,  productPrice,1);
                    System.out.println("Producto seleccionado: " + productName);
                    // Lógica para agregar a pedido o gestionar el clic
                });
                col++;
                if (col > 3) { // Cambiar fila después de 4 columnas
                    col = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }
    //Añade  los productos ala tabla cuando se clicla en ellos
    private void addOrder(String name, float precio, int cantidad){
        ProductVO addOrder= new ProductVO(name, cantidad, precio);
        boolean addBoolean=true;
        float aux=precio;
        for(ProductVO add: tablaPedido.getItems()){
            if(add.getName().equals(addOrder.getName())) {
                add.setCat(add.getCat()+1);

                add.setPre(add.getPre()+aux);
                System.out.println("veces clicladas: "+add.getCat());
                System.out.println("importe total: "+add.getPre());
                addBoolean = false;
                break;
            }
        }
        if(addBoolean) {
            tablaPedido.getItems().add(addOrder);
        }
        totalPrice();
    }
    //Calcula el total del textfield
    private void totalPrice() {
        double totalA = tablaPedido.getItems().stream().mapToDouble(ProductVO::getPre).sum();
        total.set(totalA);
    }
}
