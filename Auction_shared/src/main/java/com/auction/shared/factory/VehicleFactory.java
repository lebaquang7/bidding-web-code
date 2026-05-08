package com.auction.shared.factory;

import com.auction.shared.models.Item;
import com.auction.shared.models.Vehicle;


public class VehicleFactory implements ItemFactory{

    @Override
    public Item createItem(String name, String description, double startingPrice, String sellerId) {
        return new Vehicle(name, description, startingPrice, sellerId);
    }

}
