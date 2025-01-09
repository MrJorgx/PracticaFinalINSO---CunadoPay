package controllers;

import database.DatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.Alert; // Para mostrar cuadros de diálogo (Alert)
import javafx.scene.control.TextInputDialog; // Para mostrar un cuadro de entrada de texto
import models.ProductVO;

public class BossController {

    public  void handlerShowAllProducts() throws IOException {
        Dialog<Void> list = new Dialog<>();
        list.setTitle("Lista de Productos");
        list.setHeaderText("Productos  disponibles en la app:");

        list.setResizable(true);

        // Crear la lista de productos
        ListView<ProductVO> showList = new ListView<>();
        ObservableList<ProductVO> allProducts = showAllProducts();

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

    public void handleAddProduct(ActionEvent event) throws IOException {
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
                    if(comprobarProduct(name)){
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
                        addProduct(name,intro, auxPrice ,url);
                        int idUser= getProductIdByName(name);
                        addToStock(idUser,auxPrice,50);
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
                if (comprobar(name)) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Ya existe un usuario en la base de datos con ese nombre");
                    error.showAndWait();
                } else {
                    createBoss(name, password, 1);
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

    public void handleAddWorker(ActionEvent event) throws IOException {
        TextInputDialog add= new TextInputDialog();
        add.setHeaderText("Añadir empleado");
        add.setContentText("Añadir un nuevo empleado");
        add.setContentText("Introduce el nombre del empleado:");
        Optional<String> result = add.showAndWait();
        result.ifPresent(name-> {
            if(!name.trim().isEmpty()){
                if(comprobar(name)){
                    Alert error  = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Ya existe el empleado con ese nombre");
                    error.showAndWait();
                }else {
                    createUser(name, null);
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

    //Metodo que  devuelve true si existe ya el usuario en la base de datos
    private boolean comprobar(String nombre){
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE \"name\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement checkStmt = conn.prepareStatement(sql)) {
            // Comprobar si el usuario ya existe
            checkStmt.setString(1, nombre);
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
    //Añade jefe
    public boolean createBoss(String username, String password, int type){
        String sql = "INSERT INTO \"user\" ( \"name\", \"password\", \"type\") VALUES (?,?,?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, type);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //Añade usuario
    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO \"user\" ( \"name\", \"password\") VALUES (?, ?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Devuelve true si se inserta correctamente

        } catch (SQLException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //Comprobar si  el producto existe
    public boolean comprobarProduct(String name){
        String sql = "SELECT COUNT(*) FROM \"producto\" WHERE \"nombre\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement checkStmt = conn.prepareStatement(sql)) {
            // Comprobar si el usuario ya existe
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
    public boolean addToStock(int idproduct,  float price, int  quantity) {
        String sql = "INSERT INTO \"inventario\" (\"idProducto\", \"cantidadDisponible\", \"precio\") VALUES (?, ?, ?)";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idproduct);
            stmt.setInt(2, quantity);
            stmt.setFloat(3, price);


            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar stock al inventario: " + e.getMessage());
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
    //Mostrar todos los productos  que existen
    private ObservableList<ProductVO> showAllProducts() {
        ObservableList<ProductVO> productList = FXCollections.observableArrayList();
        String sql = "SELECT \"nombre\", \"tipo\"  FROM \"producto\" ";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("nombre");
                int category = rs.getInt("tipo");
                // Crear un nuevo objeto ProductVO y añadirlo a la lista
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
