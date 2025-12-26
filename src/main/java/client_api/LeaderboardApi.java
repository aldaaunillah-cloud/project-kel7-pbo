package client_api;

public class LeaderboardApi extends ApiClient {
    public static String topJson(String baseUrl) throws Exception {
        return get(baseUrl, "/leaderboard/top_json");
    }
}
