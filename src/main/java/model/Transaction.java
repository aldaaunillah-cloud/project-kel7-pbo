package model;

import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private int rentalId;
    private double amount;
    private LocalDateTime transactionTime;

    public Transaction() {}

    public Transaction(int id, int rentalId,
                       double amount,
                       LocalDateTime transactionTime) {
        this.id = id;
        this.rentalId = rentalId;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
