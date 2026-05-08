package com.auction.shared.models;

public abstract class Item extends Entity{
    private String name;
    private String description;
    private double startingPrice;
    private String sellerId;

    public Item(String name, String description, double startingPrice, String sellerId) {
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.sellerId = sellerId;
    }

    public Item() {
    }
}
