package client_api;

public class RentalApi extends ApiClient {

    public static String createRental(int userId, int psId, int duration) throws Exception {
        return get("/rental/create?userId=" + userId +
                   "&psId=" + psId +
                   "&duration=" + duration);
    }

    public static String getUserRentals(int userId) throws Exception {
        return get("/rental/user?userId=" + userId);
    }
}
