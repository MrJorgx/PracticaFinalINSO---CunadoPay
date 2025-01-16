package controllers;

import models.DAO.UserDAO;
import models.VO.UserVO;

import java.util.List;

public class UserController {
    private UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    public boolean comprobar(String nombre) {
        return userDAO.comprobar(nombre);
    }

    public boolean createBoss(String username, String password, int type) {
        return userDAO.createBoss(username, password, type);
    }

    public boolean createUser(String username, String password) {
        return userDAO.createUser(username, password);
    }

    public int getUserIdByName(String username) {
        return userDAO.getUserIdByName(username);
    }

    public String getUserNameById(int id) {
        return userDAO.getUserNameById(id);
    }

    public boolean userOk(String username, String password) {
        return userDAO.userOk(username, password);
    }

    public String generateColor(int id) {
        return userDAO.generateColor(id);
    }

    public List<UserVO> getUsers() {
        return userDAO.searchUsers();
    }

    public boolean check(String name) {
        return userDAO.check(name);
    }

    public static String getNameUserBoss() {
        return UserDAO.getNameUserBoss();
    }
}