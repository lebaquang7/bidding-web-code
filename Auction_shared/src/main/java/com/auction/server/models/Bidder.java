package com.auction.server.models;

public class Bidder extends User {
    private double balance;

    private static final long serialVersionUID = 1L;

    public Bidder(double balance, String userName, String passWord, int id) {
        super(userName, passWord, id);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
