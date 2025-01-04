package dao;

public class UserDAOImpl implements UserDao {
    //Base de datos, revisar
    private List<User> users = new ArrayList<>();

    public String save(User user) {
        for (User buscar : users) {
            if (buscar.getName().equals(user.name)) {
                return "No se puede añadir el usuario, nombre repetido";
            }
        }
        users.add(user);
        return "Usuario añadido  con exito";
    }

    public User findById(int id) {
        for (User buscar : users) {
            if (buscar.getId() == id) {
                return buscar;
            }
        }
        return null;
    }

    public List<User> findByName(String name) {
        List<User> imprimir = new ArrayList<>();
        for (User buscar : users) {
            if (buscar.getName().equals(name)) {
                imprimir.add(buscar);
            }
        }
        return imprimir;
    }

    public List<User> findPassword(String password) {
        List<User> imprimir = new ArrayList<>();
        for (User buscar : users) {
            if (buscar.getTipo() == 1) {
                if (buscar.getPassword().equals(password)) {
                    imprimir.add(buscar);
                }
            }
        }
        return imprimir
    }

    public List<User> findAll() {
        return users;
    }

    public void update(User user, String name, String password) {
        if (name != null) {
            user.setName(name);
        }
        if (user.getTipo() == 1) {
            if (password != null) {
                user.setPassword(password);
            }
        }
    }
    public String delete(String name) {
        for(User borrar: users){
            if(borrar.getName().equals(name)){
                User imprimir= borrar;
                users.remove(borrar);
            }
        }
        return "Se ha eliminado el usuario: " + name;

    }
}