package com.auction.shared.factory;

import com.auction.shared.models.Item;
import com.auction.shared.models.Electronics;

public class ElectronicsFactory implements ItemFactory{

    @Override
    public Item createItem(String name, String description, double startingPrice, double currentPrice,String id) {
        return new Electronics(name, description, startingPrice, currentPrice, id);
    }
}
