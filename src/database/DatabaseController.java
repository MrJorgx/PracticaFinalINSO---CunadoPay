// Configuración inicial de la base de datos
package database;
/*/


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongoDBAtlas {
    public static void main(String[] args) {
        String uri = "mongodb+srv://<db_username>:<db_password>@cunadopay.bbnff.mongodb.net/";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Conectar a la base de datos
            MongoDatabase database = mongoClient.getDatabase("nombre_base_datos");
            System.out.println("Conexión exitosa a MongoDB Atlas");

            // Probar el acceso
            System.out.println("Colecciones disponibles: " + database.listCollectionNames());
        } catch (Exception e) {
            System.err.println("Error conectando a MongoDB Atlas: " + e.getMessage());
        }
    }
}/*/


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoClientConnectionExample {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://insoFinal:inso2024@cunadopay.bbnff.mongodb.net/?retryWrites=true&w=majority&appName=CunadoPay";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
