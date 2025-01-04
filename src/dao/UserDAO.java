package dao;

import models.User;
import java.util.List;

public class UserDAO {
    String save(User user);
    User findById(int id);
    List<User>  findByName(String name);
    List<User>  findPassword(String password);
    List<User> findAll();
    void update(User user, String name, String password);
    String delete(String name);
}