package dao;

import controllers.BossController;
import controllers.InventoryController;
import controllers.DatabaseController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import models.ProductVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {

    private BossController bossController;
    private InventoryController inventoryController;
    private TableView<ProductVO> tablaPedido;
    private HBox typeButtons;
    private GridPane productPane;
    private SimpleDoubleProperty total;
    private SimpleDoubleProperty totalNoIVA;
    private StockDAO stockdao;
    private OrderDAO orderdao;

    // Constructor que recibe BossControllerInicializar tablaPedido, buttons product pane de BossController
    public ProductDAO(BossController bossController) {
        this.bossController = bossController;
        this.tablaPedido = bossController.getTablaPedido();
        this.typeButtons = bossController.getTypeButtons();
        this.productPane = bossController.getProductPane();
        this.total = bossController.getTotal();
        this.totalNoIVA = bossController.getTotalNoIVA();
    }
    //Inicializar tablaPedido, buttons product pane de InventoryController
    public ProductDAO(InventoryController inventoryController){
        this.inventoryController = inventoryController;
        this.tablaPedido = inventoryController.getTablaPedido();
        this.typeButtons = inventoryController.getTypeButtons();
        this.productPane = inventoryController.getProductPane();
        this.total = inventoryController.getTotal();
        this.totalNoIVA = inventoryController.getTotalNoIVA();
    }
    public ProductDAO(StockDAO stockdao){
        this.stockdao=stockdao;
    }
    public ProductDAO(OrderDAO orderdao){
        this.orderdao=orderdao;
    }

    public void loadCategory(){
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
    public void loadProducts(int aux){
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
    public void addOrder(String name, float precio, int cantidad){
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
    public void totalPrice() {
        double totalA = tablaPedido.getItems().stream().mapToDouble(ProductVO::getPre).sum();
        total.set(totalA);
        double aux= totalA/1.21;
        totalNoIVA.set(aux);
    }
    //Comprobar si  el producto existe
    public boolean comprobarProduct(String name){
        String sql = "SELECT COUNT(*) FROM \"producto\" WHERE \"nombre\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement checkStmt = conn.prepareStatement(sql)) {
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Devuelve true si existe, false si no
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al comprobar el usuario en la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //Añadir el producto
    public boolean addProduct(String name, int type, float price, String url){
        String sql= "INSERT INTO \"producto\" (\"nombre\", \"tipo\", \"precio\", \"url\") VALUES (?,?,?, ?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, type);
            stmt.setFloat(3, price);
            stmt.setString(4, url);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public int getProductIdByName(String username) {
        String sql = "SELECT \"idProducto\" FROM \"producto\" WHERE \"nombre\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idProducto"); // Devuelve el iduser encontrado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el ID del producto: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    public String getProductNameById(int search) {
        String sql = "SELECT \"nombre\" FROM \"producto\" WHERE \"idProducto\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, search);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre"); // Devuelve el iduser encontrado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el nombre del producto: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    //Mostrar todos los productos  que existen
    public ObservableList<ProductVO> showAllProducts() {
        ObservableList<ProductVO> productList = FXCollections.observableArrayList();
        String sql = "SELECT \"nombre\", \"tipo\"  FROM \"producto\" ";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("nombre");
                int category = rs.getInt("tipo");
                ProductVO product = new ProductVO(name, 0,0,category, "");
                productList.add(product);
            }
            return productList;
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}