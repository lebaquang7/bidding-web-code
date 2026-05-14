package com.auction.shared.factory;

import com.auction.shared.models.Item;
import com.auction.shared.models.Art;

public class ArtFactory implements ItemFactory{

    @Override
    public Item createItem(String name, String description, double startingPrice, double currentPrice) {
        return new Art(name, description, startingPrice, currentPrice);
    }
}
