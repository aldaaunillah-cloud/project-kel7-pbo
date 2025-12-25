package service;

import dao.PSDAO;
import dao.RentalDAO;

public class RentalService {

    private final RentalDAO rentalDAO = new RentalDAO();
    private final PSDAO psDAO = new PSDAO();

    public boolean createRental(int userId, int psId, int duration) {
        if (userId <= 0 || psId <= 0 || duration <= 0) {
            System.out.println("Input tidak valid");
            return false;
        }

        if (!psDAO.isAvailable(psId)) {
            System.out.println("PS tidak tersedia");
            return false;
        }

        boolean inserted = rentalDAO.createRental(userId, psId, duration);
        if (!inserted) {
            System.out.println("Gagal insert rental");
            return false;
        }

        psDAO.updateStatus(psId, "RENTED");
        return true;
    }

    // ✅ INI YANG DIBUTUHKAN RentalController
    public String getUserRentals(int userId) {
        if (userId <= 0) return "[]";
        return rentalDAO.getUserRentals(userId);
    }
}
