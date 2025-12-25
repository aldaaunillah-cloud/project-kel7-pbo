package service;

import dao.UserDAO;
import util.PasswordUtil;




public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        // hash password dulu (best practice)
        String hashedPassword = PasswordUtil.hash(password);
        return userDAO.login(username, hashedPassword);
    }
}
