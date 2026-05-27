package com.auction.shared.models;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private static List<Item> allItems = new ArrayList<>();

    public static Item getItemById(String id) {
        if (allItems == null) return null; // Bảo vệ thêm một lớp nữa

        return allItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static void setAllItems(List<Item> items) {
        allItems = items;
    }
}