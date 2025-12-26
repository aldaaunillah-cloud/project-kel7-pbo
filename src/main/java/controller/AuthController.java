package controller;

import service.AuthService;

public class AuthController {

    private static AuthService authService = new AuthService();

    public static String login(String username, String password) {
        boolean success = authService.login(username, password).isPresent();
        return success ? "LOGIN_SUCCESS" : "LOGIN_FAILED";
    }

    public static String register(String username, String password) {
        boolean success = authService.register(username, password);
        return success ? "REGISTER_SUCCESS" : "REGISTER_FAILED";
    }
}
