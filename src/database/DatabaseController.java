package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseController {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            statement = connection.createStatement();
            statement.executeQuery("SELECT 1");
            System.out.println("Pinged your deployment. You successfully connected to PostgreSQL!");

            // Aquí puedes agregar cualquier operación que quieras realizar en la base de datos

        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}