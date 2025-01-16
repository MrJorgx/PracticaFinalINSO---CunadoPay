package models.DAO;

import controllers.DatabaseController;
import models.VO.UserVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    private static String nameUserBoss;
    //Metodo que  devuelve true si existe ya el usuario en la base de datos
    public boolean comprobar(String nombre){
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE \"name\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement checkStmt = conn.prepareStatement(sql)) {
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
    //Devulve el id de un nombre
    public int getUserIdByName(String username) {
        String sql = "SELECT \"idUser\" FROM \"user\" WHERE \"name\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idUser");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el ID del empleado: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    public String getUserNameById(int id) {
        String sql = "SELECT \"name\" FROM \"user\" WHERE \"idUser\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el nombre del empleado: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    public boolean userOk(String username, String password) {
        nameUserBoss= username;
        String sql = "SELECT * FROM \"user\" WHERE \"name\" = ? AND \"password\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // Si encuentra un resultado, la autenticación es exitosa
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getNameUserBoss(){
        return nameUserBoss;
    }
    public String generateColor(int id){
        switch(id%2){
            case 0:
                return "#7BAFD4";
            case 1:
                return "grey";
            default:
                return "blue";
        }
    }
    public List<UserVO> searchUsers(){
        List<UserVO> UserList = new ArrayList<>();
        String sql="SELECT \"idUser\", \"name\", \"password\", \"type\" FROM \"user\"";
        try (Connection conn = DatabaseController.main();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("name");
                int comprobar =rs.getInt("type");
                //Si el tipo de empleado es 2 es que es empleado y no jefe
                if(comprobar==2){
                    UserVO employee = new UserVO(2, username);
                    UserList.add(employee);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return UserList;
    }

    public boolean check(String name){
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE \"name\" = ?";
        try (Connection conn = DatabaseController.main();
             PreparedStatement checkStmt = conn.prepareStatement(sql)) {
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al comprobar el usuario en la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}