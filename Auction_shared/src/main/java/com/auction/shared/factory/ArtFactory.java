package com.auction.shared.factory;

import com.auction.shared.models.Item;
import com.auction.shared.models.Art;

public class ArtFactory implements ItemFactory{

    @Override
    public Item createItem(String name, String description, double startingPrice, String sellerId) {
        return new Art(name, description, startingPrice, sellerId);
    }
}
