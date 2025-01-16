package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseController {
    // Hacemos las variables no estáticas
    private Connection connection;
    private Statement statement;

    public static Connection main() {
        DatabaseController controller = new DatabaseController();

        // Cargar las variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        try {
            // Establecer la conexión con la base de datos
            controller.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            controller.statement = controller.connection.createStatement();
            controller.statement.executeQuery("SELECT 1"); // Esto asegura que la conexión sea válida
            //System.out.println("Se ha conectado de forma correcta con la base de datos");

            // Devolver la conexión a la base de datos
            return controller.connection;

        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Si hubo un error, se devuelve null
    }
}

