package controller;

import report.ReportGenerator;
import service.RentalService;

import java.time.LocalDateTime;

public class RentalController {

    private static final RentalService rentalService = new RentalService();

    /**
     * Digunakan oleh UI (RentalFrame)
     * Alur:
     * UI -> Controller -> Service -> DAO / API
     */
    public static boolean createRental(
            int userId,
            int psId,
            String psName,
            int duration
    ) {

        // 1. Proses bisnis (service)
        boolean success = rentalService.createRental(userId, psId, duration);

        if (!success) return false;

        // 2. Generate laporan pembayaran (PDF)
        try {
            double pricePerHour = 10000; // bisa dari DB nanti
            double total = duration * pricePerHour;

            ReportGenerator.generatePaymentReceipt(
                    userId,
                    psName,
                    duration,
                    pricePerHour,
                    total,
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            // PDF gagal tidak boleh membatalkan transaksi
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Untuk halaman riwayat / laporan
     */
    public static String getUserRentals(int userId) {
        return rentalService.getUserRentals(userId);
    }
}
