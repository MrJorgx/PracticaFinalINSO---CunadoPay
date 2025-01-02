package controllers;

public class LoginController {

    public boolean login(String username, String password) {
        // verificar credenciales del usuario
        // true si las credenciales son correctas, sino false
        return authenticate(username, password);
    }

    public void logout() {
        // cerrar sesión
    }

    private boolean authenticate(String username, String password) {
        // autenticación (verificar base de datos)
        // Este es un método privado auxiliar
        return "user".equals(username) && "pass".equals(password);
    }
}