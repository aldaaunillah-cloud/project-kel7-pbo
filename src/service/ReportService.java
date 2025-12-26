package service;

import dao.LeaderboardDAO;
import java.util.Map;

public class ReportService {

    public Map<String,Integer> getLeaderboard() {
        return new LeaderboardDAO().getTopUsers();
    }
}
