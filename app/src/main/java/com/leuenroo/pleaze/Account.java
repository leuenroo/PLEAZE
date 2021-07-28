package com.leuenroo.pleaze;

public class Account {
    private String firstName, lastName, phone, email, reservation;
    private boolean parked;
    private double credit;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getParked() {
        return parked;
    }

    public void setParked(Boolean parked) {
        this.parked = parked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCredit() {
        return credit;
    }

    public String getReservation() {
        return reservation;
    }

    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public Account(String firstName, String lastName, String phone, String email, Boolean parked) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.credit = 0;
        this.parked = parked;
    }

    public Account(String firstName, String lastName, String phone, String email, double credit, String reservation, boolean parked) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.credit = credit;
        this.reservation = reservation;
        this.parked = parked;
    }
}
