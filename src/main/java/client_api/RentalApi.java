package client_api;

public class RentalApi extends ApiClient {

    // kompatibilitas lama
    public static String createRental(int userId, int psId, int duration) throws Exception {
        return get("/rental/create?userId=" + userId +
                "&psId=" + psId +
                "&duration=" + duration);
    }

    public static String getUserRentals(int userId) throws Exception {
        return get("/rental/user?userId=" + userId);
    }

    // ===== REFRACTOR JSON =====
    public static String createRentalJson(int userId, int psId, int duration, String baseUrl) throws Exception {
        return get(baseUrl, "/rental/create_json?userId=" + userId +
                "&psId=" + psId +
                "&duration=" + duration);
    }

    public static String getUserRentals(int userId, String baseUrl) throws Exception {
        return get(baseUrl, "/rental/user?userId=" + userId);
    }
}
