package model;

public class LeaderboardEntry {

    private int userId;
    private int totalPoints;

    public LeaderboardEntry() {}

    public LeaderboardEntry(int userId, int totalPoints) {
        this.userId = userId;
        this.totalPoints = totalPoints;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}
