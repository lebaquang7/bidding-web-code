package com.auction.shared.models;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Seller extends User implements Serializable{
    private List<Item> itemsForSale;

    private static final long serialVersionUID = 1L;

    public Seller(String userName, String passWord) {
        super(userName, passWord);
        this.itemsForSale = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (itemsForSale == null) {this.itemsForSale = new ArrayList<>();}
        this.itemsForSale.add(item);
    }

    public List<Item> getItem() {
        return itemsForSale;
    }

    public void setItemsForSale(List<Item> items) {
        this.itemsForSale = items;
    }
}
