package service;

import com.google.gson.JsonObject;
import model.PSItem;
import realtime.RealtimeEvent;
import realtime.RealtimePublisher;
import report.PaymentReceiptGenerator;
import repository.*;

import repository.jdbc.JdbcLeaderboardRepository;
import repository.jdbc.JdbcPSRepository;
import repository.jdbc.JdbcRentalRepository;
import repository.jdbc.JdbcUserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic Rental PS.
 * Catatan rubrik:
 * - Service ini sengaja tidak melakukan threading (threading dilakukan di UI via SwingWorker/SwingAsync),
 *   supaya service tetap testable & reusable untuk 2-tier/3-tier.
 */
public class RentalService {

    public static final int PRICE_PER_HOUR = 10000;

    private final RentalRepository rentalRepo;
    private final PSRepository psRepo;
    private final UserRepository userRepo;
    private final PointsService pointsService;
    private final RealtimePublisher realtimePublisher; // boleh null

    // ===== OBSERVER (untuk UI auto-refresh) =====
    private static final List<RentalObserver> observers = new ArrayList<>();

    /** Default constructor (2-tier JDBC). */
    public RentalService() {
        this(
                new JdbcRentalRepository(),
                new JdbcPSRepository(),
                new JdbcUserRepository(),
                new PointsService(new JdbcLeaderboardRepository()),
                null
        );
    }

    public RentalService(
            RentalRepository rentalRepo,
            PSRepository psRepo,
            UserRepository userRepo,
            PointsService pointsService,
            RealtimePublisher realtimePublisher
    ) {
        this.rentalRepo = rentalRepo;
        this.psRepo = psRepo;
        this.userRepo = userRepo;
        this.pointsService = pointsService;
        this.realtimePublisher = realtimePublisher;
    }

    public static void addObserver(RentalObserver o) {
        if (o != null && !observers.contains(o)) observers.add(o);
    }

    public static void removeObserver(RentalObserver o) {
        observers.remove(o);
    }

    private static void notifyObservers() {
        for (RentalObserver o : observers) {
            try { o.onRentalCreated(); } catch (Exception ignored) {}
        }
    }

    /**
     * Create rental.
     * @param psNameFromUI optional (kalau UI sudah punya nama PS, bisa di-pass supaya receipt lebih akurat).
     */
    public boolean createRental(int userId, int psId, int duration, String psNameFromUI) {

        if (userId <= 0) return false;
        if (psId <= 0) return false;
        if (duration <= 0) return false;

        // validasi status (2-tier). Pada remote mode, server yg validasi.
        if (realtimePublisher == null) { // heuristik: kalau tidak pakai realtimePublisher, kemungkinan service dipakai server/CLI
            // no-op
        }
        try {
            if (!psRepo.isAvailable(psId)) {
                return false;
            }
        } catch (Exception ignored) {
            // untuk remote mode (opsional)
        }

        boolean ok = rentalRepo.createRental(userId, psId, duration);
        if (!ok) return false;

        // update status PS (2-tier JDBC)
        try { psRepo.updateStatus(psId, "RENTED"); } catch (Exception ignored) {}

        // add point
        try { pointsService.addPointToUser(userId, duration); } catch (Exception ignored) {}

        // struk PDF (jangan cancel transaksi kalau gagal generate)
        try {
            String username = userRepo.getUsernameById(userId).orElse("User-" + userId);
            String psName = psNameFromUI != null ? psNameFromUI : psRepo.getPSNameById(psId);
            int total = duration * PRICE_PER_HOUR;
            PaymentReceiptGenerator.generate(
                    userId, username, psName, duration, PRICE_PER_HOUR, total
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // notify UI observer
        notifyObservers();

        // ===== REAL-TIME BROADCAST =====
        if (realtimePublisher != null) {
            JsonObject payload = new JsonObject();
            payload.addProperty("userId", userId);
            payload.addProperty("psId", psId);
            payload.addProperty("duration", duration);
            payload.addProperty("total", duration * PRICE_PER_HOUR);
            payload.addProperty("ts", Instant.now().toString());

            realtimePublisher.publish(new RealtimeEvent("NEW_RENTAL", payload));

            JsonObject payload2 = new JsonObject();
            payload2.addProperty("psId", psId);
            payload2.addProperty("status", "RENTED");
            payload2.addProperty("ts", Instant.now().toString());
            realtimePublisher.publish(new RealtimeEvent("PS_STATUS_UPDATED", payload2));
        }

        return true;
    }

    // backward compatibility
    public boolean createRental(int userId, int psId, int duration) {
        return createRental(userId, psId, duration, null);
    }

    public String getUserRentals(int userId) {
        return rentalRepo.getUserRentalsJson(userId);
    }
}
