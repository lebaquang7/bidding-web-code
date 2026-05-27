package com.auction.shared.factory;

import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import java.math.BigDecimal;

public class ElectronicsFactory implements ItemFactory {

  @Override
  public Item createItem(
      String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
    return new Electronics(name, description, startingPrice, currentPrice, 24, "", "", "");
  }
}
