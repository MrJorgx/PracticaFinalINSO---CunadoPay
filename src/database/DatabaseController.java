// Configuración inicial de la base de datos

package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {
 
    private static final String DB_URL = "jdbc:sqlite:src/database/db.sqlite";

    public static void initializeDatabase() {

        try() {
            
        }catch(SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        initializeDatabase();
    }
}
