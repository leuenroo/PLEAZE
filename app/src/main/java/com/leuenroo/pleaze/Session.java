package com.leuenroo.pleaze;

public class Session {
    String userID, startTime, endTime;
    int spotNumber;
    boolean premium;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    double rate, total;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Session(String userID, String startTime, int spotNumber, double rate, boolean premium) {
        this.userID = userID;
        this.startTime = startTime;
        this.spotNumber = spotNumber;
        this.rate = rate;
        this.premium = premium;
    }

    public Session(String userID, String startTime, String endTime, int spotNumber, double rate, double total, boolean premium) {
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.spotNumber = spotNumber;
        this.rate = rate;
        this.total = total;
        this.premium = premium;
    }
}
