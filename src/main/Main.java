//Clase principal para la ejecución de la aplicación

package main;
import database.DatabaseController;
public class Main {
    public static void main(String[] args) {
        DatabaseController.main();
        App.main(args);

    }
}