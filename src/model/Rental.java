package model;

import java.time.LocalDateTime;

public class Rental {

    private int id;
    private int userId;
    private int psId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;

    public Rental() {}

    public Rental(int id, int userId, int psId,
                  LocalDateTime startTime,
                  LocalDateTime endTime,
                  double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.psId = psId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPsId() {
        return psId;
    }

    public void setPsId(int psId) {
        this.psId = psId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
