package com.auction.client.services;

import java.math.BigDecimal;

import com.auction.shared.models.Art;
import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import com.auction.shared.models.Vehicle;

public class ItemFactory {
    public static Item createItem(String type, String name, String description, BigDecimal startingPrice) {
        BigDecimal currentPrice = startingPrice;

        return switch (type) {
            case "Artwork" -> new Art(name, description, startingPrice, currentPrice, "", true, 0, "");
            case "Electronics" -> new Electronics(name, description, startingPrice, currentPrice, 24, "", "", "");
            default -> new Vehicle(name, description, startingPrice, currentPrice, "", 0, 0);
        };
    }
}
