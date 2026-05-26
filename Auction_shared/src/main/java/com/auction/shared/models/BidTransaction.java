package com.auction.shared.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BidTransaction extends Entity implements Serializable{
    private double bidAmount;
    private LocalDateTime bidTime;
    private User bidder;
    private String auctionId;

    public BidTransaction(double bidAmount, User bidder, String auctionId) {
        super();
        this.bidAmount = bidAmount;
        this.bidTime = LocalDateTime.now();
        this.auctionId = auctionId;
        this.bidder = bidder;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public String getAuctionId() { return auctionId; }

    public User getBidder() {
        return bidder;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }
}
