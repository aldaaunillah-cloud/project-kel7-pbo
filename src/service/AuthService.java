package service;

import dao.UserDAO;
import util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        String hashedPassword = PasswordUtil.hash(password);
        return userDAO.login(username, hashedPassword);
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;

        String hashedPassword = PasswordUtil.hash(password);
        return userDAO.register(username.trim(), hashedPassword);
    }
}
