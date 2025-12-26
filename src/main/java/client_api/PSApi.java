package client_api;

public class PSApi extends ApiClient {

    // lama: list string
    public static String getAll() throws Exception {
        return get("/ps/all");
    }

    public static String getAvailable() throws Exception {
        return get("/ps/available");
    }

    // ===== REFRACTOR: list PS full (id,name,status) =====
    public static String listJson(String baseUrl) throws Exception {
        return get(baseUrl, "/ps/list_json");
    }

    // ===== REFRACTOR: list PSItem (id + name) untuk combo =====
    public static String availableItemsJson(String baseUrl) throws Exception {
        return get(baseUrl, "/ps/available_items_json");
    }
}
