package com.auction.shared.factory;

import java.math.BigDecimal;

import com.auction.shared.models.Item;

public interface ItemFactory {
    // tránh tạo 1 item mà thông tin item không minh bạch
    Item createItem(String name, String description, BigDecimal startingPrice, BigDecimal currentPrice);
}
