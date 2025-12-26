package repository.jdbc;

import dao.LeaderboardDAO;
import repository.DataAccessException;
import repository.LeaderboardRepository;

import java.util.Map;

public class JdbcLeaderboardRepository implements LeaderboardRepository {

    private final LeaderboardDAO dao = new LeaderboardDAO();

    @Override
    public void addPoint(int userId, int delta) throws DataAccessException {
        try {
            dao.addPoint(userId, delta);
        } catch (Exception e) {
            throw new DataAccessException("Gagal menambah poin leaderboard.", e);
        }
    }

    @Override
    public Map<String, Integer> getTopUsers() throws DataAccessException {
        try {
            return dao.getTopUsers();
        } catch (Exception e) {
            throw new DataAccessException("Gagal mengambil leaderboard.", e);
        }
    }
}
