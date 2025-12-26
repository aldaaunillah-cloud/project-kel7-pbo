package service;

import dao.*;
import report.PaymentReceiptGenerator;
import java.util.ArrayList;
import java.util.List;

public class RentalService {

    private final RentalDAO rentalDAO = new RentalDAO();
    private final PSDAO psDAO = new PSDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PointsService pointsService = new PointsService();

    // ===== OBSERVER =====
    private static final List<RentalObserver> observers = new ArrayList<>();

    public static void addObserver(RentalObserver o) {
        observers.add(o);
    }

    private void notifyObservers() {
        observers.forEach(RentalObserver::onRentalCreated);
    }

    public boolean createRental(int userId, int psId, int duration) {

        // ===== VALIDASI =====
        if (userId <= 0 || psId <= 0 || duration <= 0) {
            System.err.println("Input tidak valid");
            return false;
        }

        // ===== CEK PS =====
        if (!psDAO.isAvailable(psId)) {
            System.err.println("PS tidak tersedia");
            return false;
        }

        // ===== SIMPAN RENTAL =====
        if (!rentalDAO.createRental(userId, psId, duration)) {
            System.err.println("Gagal simpan rental");
            return false;
        }

        // ===== UPDATE STATUS PS =====
        psDAO.updateStatus(psId, "RENTED");

        // ===== POINT =====
        try {
            pointsService.addPointToUser(userId, duration);
        } catch (Exception e) {
            System.err.println("Gagal menambah poin (tidak fatal)");
        }

        // ===== STRUK PDF =====
        try {
            int pricePerHour = 10000;
            PaymentReceiptGenerator.generate(
                    userId,
                    userDAO.getUsernameById(userId),
                    psDAO.getPSNameById(psId),
                    duration,
                    pricePerHour,
                    duration * pricePerHour
            );
        } catch (Exception e) {
            System.err.println("Gagal generate PDF");
        }

        // 🔥 REALTIME UPDATE
        notifyObservers();

        return true;
    }

    public String getUserRentals(int userId) {
        if (userId <= 0) return "[]";
        return rentalDAO.getUserRentals(userId);
    }
}
