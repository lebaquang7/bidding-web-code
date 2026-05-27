package com.auction.shared.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Auction implements Serializable {
    private int id;
    private Item item;
    private double currentPrice;
    private double startPrice;
    private User seller;
    private User highestBidder;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;

    public Auction(int id, Item item, double startPrice, User seller, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.item = item;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.seller = seller;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.OPEN;
    }

    public int getId() {
        return id;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Item getItem() {
        return item;
    }

    public User getSeller() {
        return seller;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(User highestBidder) {
        this.highestBidder = highestBidder;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }
}
