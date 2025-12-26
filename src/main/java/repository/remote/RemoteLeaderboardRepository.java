package repository.remote;

import app.AppConfig;
import client_api.LeaderboardApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import repository.DataAccessException;
import repository.LeaderboardRepository;

import java.lang.reflect.Type;
import java.util.Map;

public class RemoteLeaderboardRepository implements LeaderboardRepository {

    private final Gson gson = new Gson();

    @Override
    public void addPoint(int userId, int delta) throws DataAccessException {
        // biasanya server yang add point saat create rental, client tidak perlu.
    }

    @Override
    public Map<String, Integer> getTopUsers() throws DataAccessException {
        try {
            String json = LeaderboardApi.topJson(AppConfig.API_BASE_URL);
            Type t = new TypeToken<Map<String, Integer>>() {}.getType();
            return gson.fromJson(json, t);
        } catch (Exception e) {
            throw new DataAccessException("Gagal load leaderboard dari server.", e);
        }
    }
}
