package service;

import dao.RentalDAO;

public class RentalService {

    private final RentalDAO rentalDAO = new RentalDAO();

    public boolean rentPS(int userId, int psId, int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("Durasi sewa tidak valid");
        }
        return rentalDAO.createRental(userId, psId, hours);
    }
}
