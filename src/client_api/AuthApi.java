package client_api;

public class AuthApi extends ApiClient {

    public static String login(String username, String password) throws Exception {
        return get("/auth/login?username=" + username + "&password=" + password);
    }

    public static String register(String username, String password) throws Exception {
        return get("/auth/register?username=" + username + "&password=" + password);
    }
}
