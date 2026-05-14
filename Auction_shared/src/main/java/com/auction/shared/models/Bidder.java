package com.auction.shared.models;

public class Bidder extends User {
    private double balance;
    private String shippingAddress;
    private int reputationScore; // đc tính sau những lần đấu giá

    private static final long serialVersionUID = 1L;

    public Bidder(String userName, String passWord, String id, String shippingAddress, double balance, int reputationScore) {
        super(userName, passWord, id);
        this.balance = balance;
        this.shippingAddress = shippingAddress;
        this.reputationScore = reputationScore;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getShippingAddress(){
        return shippingAddress;
    }
    public void setShippingAddress(String shippingAddress){
        this.shippingAddress = shippingAddress;
    }

    public void deposit(double amount) {
        if (amount > 0) this.balance += amount;
    }

    public int getReputationScore() {return reputationScore;}

}
