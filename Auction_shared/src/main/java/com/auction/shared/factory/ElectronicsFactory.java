package com.auction.shared.factory;

import java.math.BigDecimal;

import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;

public class ElectronicsFactory implements ItemFactory{

    @Override
    public Item createItem(String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
        return new Electronics(name, description, startingPrice, currentPrice);
    }
}
