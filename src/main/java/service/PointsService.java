package service;

import repository.LeaderboardRepository;

/**
 * Aturan poin:
 * contoh: 10 poin per jam sewa (bisa disesuaikan rubrik).
 */
public class PointsService {

    private final LeaderboardRepository leaderboardRepo;

    public PointsService(LeaderboardRepository leaderboardRepo) {
        this.leaderboardRepo = leaderboardRepo;
    }

    public void addPointToUser(int userId, int hours) {
        leaderboardRepo.addPoint(userId, hours * 10);
    }
}
