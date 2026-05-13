package com.auction.shared.models;

public abstract class Item extends Entity {
    private String name;
    private String description;
    private final double startingPrice;
    private double currentPrice;

    private static final long serialVersionUID = 1L;

    public Item(String name, String description, double startingPrice, double currentPrice, String id) {
        super(id);
        this.name = name;
        this.currentPrice = currentPrice;
        this.startingPrice = startingPrice;
        this.description = description;
    }

    public String getItemName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription() {
        this.description = description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

}
