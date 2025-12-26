package client_api;

public class AuthApi extends ApiClient {

    // kompatibilitas lama (return "LOGIN_SUCCESS"/"LOGIN_FAILED")
    public static String login(String username, String password) throws Exception {
        return get("/auth/login?username=" + enc(username) + "&password=" + enc(password));
    }

    // ===== REFRACTOR: return JSON {ok,userId,username} =====
    public static String loginJson(String username, String hashedPassword, String baseUrl) throws Exception {
        return get(baseUrl, "/auth/login_json?username=" + enc(username) + "&password=" + enc(hashedPassword));
    }

    public static String registerJson(String username, String hashedPassword, String baseUrl) throws Exception {
        return get(baseUrl, "/auth/register_json?username=" + enc(username) + "&password=" + enc(hashedPassword));
    }

    private static String enc(String s) {
        try { return java.net.URLEncoder.encode(s, "UTF-8"); }
        catch (Exception e) { return s; }
    }
}
