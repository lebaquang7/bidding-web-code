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

    private double maxBid = 0;
    private double increment = 0;

    public double getMaxBid() { return maxBid; }
    public void setMaxBid(double maxBid) { this.maxBid = maxBid; }
    public double getIncrement() { return increment; }
    public void setIncrement(double increment) { this.increment = increment; }

    private String tempUsername;
    private String tempAuctionId;

    public String getTempUsername() { return tempUsername; }
    public void setTempUsername(String tempUsername) { this.tempUsername = tempUsername; }
    public String getTempAuctionId() { return tempAuctionId; }
    public void setTempAuctionId(String tempAuctionId) { this.tempAuctionId = tempAuctionId; }
}
