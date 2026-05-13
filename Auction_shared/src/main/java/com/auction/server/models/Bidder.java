package com.auction.server.models;

import java.io.Serializable;

public class Bidder extends User implements Serializable{
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

    public void addBalance(double balance) {
        this.balance += balance;
    }
}
