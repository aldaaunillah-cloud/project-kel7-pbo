package controller;

import service.RentalService;

public class RentalController {

    private static RentalService rentalService = new RentalService();

    public static String createRental(int userId, int psId, int duration) {
        boolean success = rentalService.createRental(userId, psId, duration);
        return success ? "RENTAL_CREATED" : "RENTAL_FAILED";
    }

    public static String getUserRentals(int userId) {
        return rentalService.getUserRentals(userId);
    }
}
