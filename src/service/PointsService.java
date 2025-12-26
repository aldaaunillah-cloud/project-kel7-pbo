package service;

import dao.LeaderboardDAO;

public class PointsService {

    private final LeaderboardDAO dao = new LeaderboardDAO();

    public void addPointToUser(int userId, int hours) {
        dao.addPoint(userId, hours * 10);
    }
}
