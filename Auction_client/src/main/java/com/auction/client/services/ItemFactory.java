package com.auction.client.services;

import com.auction.shared.models.Art;
import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import com.auction.shared.models.Vehicle;
import java.math.BigDecimal;

public class ItemFactory {
  // Class xử lý việc tạo sản phẩm
  /**
   * Usage: Tạo sản phẩm
   *
   * @param type Loại sản phẩm
   * @param name Tên sản phẩm
   * @param description Description sản phẩm
   * @param startingPrice Giá khởi điểm sản phẩm
   * @return
   */
  public static Item createItem(
      String type, String name, String description, BigDecimal startingPrice) {
    BigDecimal currentPrice = startingPrice;

    return switch (type) {
      case "Artwork" -> new Art(name, description, startingPrice, currentPrice, "", true, 0, "");
      case "Electronics" ->
          new Electronics(name, description, startingPrice, currentPrice, 24, "", "", "");
      default -> new Vehicle(name, description, startingPrice, currentPrice, "", 0, 0);
    };
  }
}
