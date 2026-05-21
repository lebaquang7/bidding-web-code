package com.auction.shared.models;

import java.math.BigDecimal;

public abstract class Item extends Entity {
    private String name;
    private String description;
    private final BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private String sellerId;

    private BigDecimal priceIncrement;

    private static final long serialVersionUID = 1L;

    public Item(String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
        super();
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

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getSellerId() {return sellerId;}
    public void setSellerId(String id) {this.sellerId = id;}
    public BigDecimal getPriceIncrement() {
        return priceIncrement;
    }

    public void setPriceIncrement(BigDecimal priceIncrement) {
        this.priceIncrement = priceIncrement;
    }
}
