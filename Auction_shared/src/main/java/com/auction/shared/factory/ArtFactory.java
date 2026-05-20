package com.auction.shared.factory;

import java.math.BigDecimal;

import com.auction.shared.models.Art;
import com.auction.shared.models.Item;

public class ArtFactory implements ItemFactory{

    @Override
    public Item createItem(String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
        return new Art(name, description, startingPrice, currentPrice);
    }
}
