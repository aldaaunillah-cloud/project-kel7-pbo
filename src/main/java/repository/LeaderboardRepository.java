package repository;

import java.util.Map;

public interface LeaderboardRepository {
    void addPoint(int userId, int delta) throws DataAccessException;
    Map<String, Integer> getTopUsers() throws DataAccessException;
}
