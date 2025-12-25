package service;

import dao.LeaderboardDAO;
import java.util.Map;

public class ReportService {

    private final LeaderboardDAO leaderboardDAO = new LeaderboardDAO();

    public Map<String, Integer> getLeaderboardData() {
        return leaderboardDAO.getTopUsers();
    }
}
