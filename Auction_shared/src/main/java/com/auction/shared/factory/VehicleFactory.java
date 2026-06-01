package com.auction.shared.factory;

import com.auction.shared.models.Item;
import com.auction.shared.models.Vehicle;
import java.math.BigDecimal;

public class VehicleFactory implements ItemFactory {

  @Override
  public Item createItem(
      String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
    return new Vehicle(name, description, startingPrice, currentPrice, "", 0, 0);
  }
}
