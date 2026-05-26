package com.auction.shared.models;

public class Bidder extends User {
    private double balance;
    private String shippingAddress;
    private int reputationScore; // đc tính sau những lần đấu giá

    private static final long serialVersionUID = 1L;

    public Bidder(String userName, String passWord, String email, String shippingAddress, int reputationScore) {
        super(userName, passWord, email);
        this.balance = 0.0;
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

    public int getReputationScore() {return reputationScore;}

}
