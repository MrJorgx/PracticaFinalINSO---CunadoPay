package controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import models.DAO.ProductDAO;
import models.VO.ProductVO;

import java.util.List;

public class ProductController {
    private ProductDAO productDAO= new ProductDAO();

    private HBox typeButtons;

    private GridPane productPane;

    private TableView<ProductVO> tablaPedido;

    private BossController bossController;
    private SimpleDoubleProperty total;
    private SimpleDoubleProperty totalNoIVA;


    public ProductController(BossController bossController) {
        this.bossController = bossController;
        this.productDAO = new ProductDAO();
        this.tablaPedido = bossController.getTablaPedido();
        this.typeButtons = bossController.getTypeButtons();
        this.productPane = bossController.getProductPane();
        this.total =        bossController.getTotal();
        this.totalNoIVA = bossController.getTotalNoIVA();
    }


    public ProductController(InventoryController inventoryController) {

        this.productDAO = new ProductDAO();
        this.tablaPedido = inventoryController.getTablaPedido();
        this.typeButtons = inventoryController.getTypeButtons();
        this.productPane = inventoryController.getProductPane();
        this.total =        inventoryController.getTotal();
        this.totalNoIVA = inventoryController.getTotalNoIVA();
    }

    public void loadCategory() {
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

    public void loadProducts(int aux) {
        productPane.getChildren().clear();
        productPane.setVisible(true);
        List<ProductVO> list =productDAO.loadProducts(aux);
        int row=0;
        int col=0;
        for(ProductVO productVO : list){

            Button productButton= new Button(productVO.getName());
            productButton.setStyle("-fx-min-width: 100px; -fx-min-height: 40px; -fx-background-color: lightblue;");
            productPane.add(productButton, col, row);
            productButton.setOnAction(event -> {
                addOrder(productVO.getName(),  productVO.getPrice(),1);
                System.out.println("Producto seleccionado: " + productVO.getName());
            });
            col++;
            if (col > 3) { // Cambiar fila después de 4 columnas
                col = 0;
                row++;
            }

        }
    }

    public void addOrder(String name, float price, int quantity) {
        ProductVO addOrder= new ProductVO(name, quantity, price);
        boolean addBoolean=true;
        float aux=price;
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

    public boolean comprobarProduct(String name) {
        return productDAO.comprobarProduct(name);
    }

    public boolean addProduct(String name, int type, float price, String url) {
        return productDAO.addProduct(name, type, price, url);
    }

    public int getProductIdByName(String username) {
        return productDAO.getProductIdByName(username);
    }

    public String getProductNameById(int search) {
        return productDAO.getProductNameById(search);
    }

    public ObservableList<ProductVO> showAllProducts() {
        return productDAO.showAllProducts();
    }

    public List<ProductVO> recuperar(int idMesa) {
        return productDAO.recuperar(idMesa);
    }
}