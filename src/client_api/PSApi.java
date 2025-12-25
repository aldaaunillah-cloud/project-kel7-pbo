package client_api;

public class PSApi extends ApiClient {

    public static String getAllPS() throws Exception {
        return get("/ps");
    }

    public static String getAvailablePS() throws Exception {
        return get("/ps/available");
    }
}
