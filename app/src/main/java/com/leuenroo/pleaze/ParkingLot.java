package com.leuenroo.pleaze;

import java.util.List;

public class ParkingLot {
    int totalSpots, availableSpots;
    List<Boolean> spots;

    public int getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(int totalSpots) {
        this.totalSpots = totalSpots;
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    public List<Boolean> getSpots() {
        return spots;
    }

    public void setSpots(List<Boolean> spots) {
        this.spots = spots;
    }

    public ParkingLot(int totalSpots, int availableSpots, List<Boolean> spots) {
        this.totalSpots = totalSpots;
        this.availableSpots = availableSpots;
        this.spots = spots;
    }
}
