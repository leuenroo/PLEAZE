package com.leuenroo.pleaze;

public class Reservation {
    String userID, date, time;
    int spotNumber;
    boolean parked;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    public boolean isParked() {
        return parked;
    }

    public void setParked(boolean parked) {
        this.parked = parked;
    }

    public Reservation(String userID, String date, String time, int spotNumber, boolean parked) {
        this.userID = userID;
        this.date = date;
        this.time = time;
        this.spotNumber = spotNumber;
        this.parked = parked;
    }
}
