package com.auction.server.models;

import java.io.Serializable;

public class Auction implements Serializable {
    private int id;
    private Item item;
    private double currentPrice;
    private double startPrice;
    private User seller;

    public Auction(int id, Item item, double startPrice, User seller) {
        this.id = id;
        this.item = item;
        this.startPrice = startPrice;
        this.currentPrice = startPrice; // Lúc mới đầu, giá hiện tại = giá khởi điểm
        this.seller = seller;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public Item getItem() {
        return item;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
