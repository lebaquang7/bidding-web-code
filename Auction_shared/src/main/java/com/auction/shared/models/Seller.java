package com.auction.shared.models;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User {
    private List<Item> itemsForSale;

    private static final long serialVersionUID = 1L;

    public Seller(String userName, String passWord, int id) {
        super(userName, passWord, id);
        this.itemsForSale = new ArrayList<>();
    }

    public void addItem(Item item) {
        itemsForSale.add(item);
    }

    public List<Item> getItem() {
        return itemsForSale;
    }
}
