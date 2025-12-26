package app;

import realtime.RealtimeClient;
import repository.*;
import repository.jdbc.*;
import repository.remote.*;
import service.*;

/**
 * Simple dependency container (manual DI) untuk switch 2-tier / 3-tier.
 */
public class AppContext {

    public final UserRepository userRepo;
    public final PSRepository psRepo;
    public final RentalRepository rentalRepo;
    public final LeaderboardRepository leaderboardRepo;

    public final RealtimeClient realtimeClient;

    public final AuthService authService;
    public final PSService psService;
    public final ReportService reportService;
    public final RentalService rentalService;

    private AppContext(
            UserRepository userRepo,
            PSRepository psRepo,
            RentalRepository rentalRepo,
            LeaderboardRepository leaderboardRepo,
            RealtimeClient realtimeClient
    ) {
        this.userRepo = userRepo;
        this.psRepo = psRepo;
        this.rentalRepo = rentalRepo;
        this.leaderboardRepo = leaderboardRepo;
        this.realtimeClient = realtimeClient;

        this.authService = new AuthService(userRepo);
        this.psService = new PSService(psRepo);
        this.reportService = new ReportService(leaderboardRepo);

        // RentalService butuh points + realtime publisher
        PointsService pointsService = new PointsService(leaderboardRepo);
        this.rentalService = new RentalService(rentalRepo, psRepo, userRepo, pointsService, realtimeClient);
    }

    public static AppContext create() {
        try {
            RealtimeClient ws = new RealtimeClient();

            if (AppConfig.IS_3_TIER) {
                return new AppContext(
                        new RemoteUserRepository(),
                        new RemotePSRepository(),
                        new RemoteRentalRepository(),
                        new RemoteLeaderboardRepository(),
                        ws
                );
            }

            return new AppContext(
                    new JdbcUserRepository(),
                    new JdbcPSRepository(),
                    new JdbcRentalRepository(),
                    new JdbcLeaderboardRepository(),
                    ws
            );
        } catch (Exception e) {
            throw new RuntimeException("Gagal inisialisasi AppContext (RealtimeClient).", e);
        }
    }
}
