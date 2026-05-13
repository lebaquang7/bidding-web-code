package com.auction.shared.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BidTransaction implements Serializable{
    private double bidAmount;
    private LocalDateTime bidTime;
    private User bidder;
    private Auction auction;

    public BidTransaction(double bidAmount, LocalDateTime bidTime, User bidder, Auction auction) {
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.auction = auction;
        this.bidder = bidder;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public Auction getAuction() {
        return auction;
    }

    public User getBidder() {
        return bidder;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }
}
