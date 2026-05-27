package com.auction.shared.factory;

import com.auction.shared.models.Art;
import com.auction.shared.models.Item;
import java.math.BigDecimal;

public class ArtFactory implements ItemFactory {

  @Override
  public Item createItem(
      String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
    return new Art(name, description, startingPrice, currentPrice, "", true, 0, "");
  }
}
