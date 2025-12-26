package service;

import repository.LeaderboardRepository;

import java.util.Map;

public class ReportService {

    private final LeaderboardRepository leaderboardRepo;

    public ReportService() {
        this(new repository.jdbc.JdbcLeaderboardRepository());
    }

    public ReportService(LeaderboardRepository leaderboardRepo) {
        this.leaderboardRepo = leaderboardRepo;
    }

    public Map<String, Integer> getLeaderboard() {
        return leaderboardRepo.getTopUsers();
    }
}
