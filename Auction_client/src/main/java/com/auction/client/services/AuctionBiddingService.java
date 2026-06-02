package com.auction.client.services;

import com.auction.client.controllers.AuctionViewController;
import com.auction.client.controllers.ItemDetailsController;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Item;
import java.math.BigDecimal;
import java.util.Map;

public class AuctionBiddingService {
  public static boolean isValidBidAmount(Item item, BigDecimal bidAmount) {
    return bidAmount.compareTo(calculateMinimumRequiredBid(item)) >= 0;
  }

  public static BigDecimal calculateMinimumRequiredBid(Item item) {
    BigDecimal currentPrice = item.getCurrentPrice();
    BigDecimal incrementPercent = item.getPriceIncrement();
    return currentPrice.add(currentPrice.multiply(incrementPercent).divide(new BigDecimal("100")));
  }

  @SuppressWarnings("unchecked")
  public static void processIncomingNotification(Object message, Item currentItem, AuctionViewController controller) {
    if (message instanceof BidTransaction tx) {
      if (currentItem != null && tx.getItemId().equals(currentItem.getId())) {
        currentItem.setCurrentPrice(tx.getBidAmount());
        currentItem.setHighestBidderId(tx.getBidderId());
      }
    } else if (message instanceof Map) {
      Map<String, Object> data = (Map<String, Object>) message;
      String type = (String) data.get("type");

      if ("TIME_UPDATE".equals(type)) {
        String sessionId = String.valueOf(data.get("sessionId")).trim();
        if (currentItem != null && currentItem.getId().equals(sessionId)) {
          Object valObj = data.get("value");
          if (valObj instanceof Number num) {
            controller.updateRemainingTime(num.intValue());
          }
        }
      } else if ("END_AUCTION".equals(type)) {
        String sessionId = String.valueOf(data.get("sessionId")).trim();
        if (currentItem != null && currentItem.getId().equals(sessionId)) {
          String winner = (String) data.get("winnerName");
          controller.handleAuctionEndEvent(winner);
        }
      }
    }
  }

  public static void processIncomingNotification(Object message, Item currentItem, ItemDetailsController controller) {
    if (message instanceof BidTransaction tx) {
      if (currentItem != null && tx.getItemId().equals(currentItem.getId())) {
        currentItem.setCurrentPrice(tx.getBidAmount());
        currentItem.setHighestBidderId(tx.getBidderId());
      }
    } else if (message instanceof Map) {
      Map<String, Object> data = (Map<String, Object>) message;
      String type = (String) data.get("type");

      if ("TIME_UPDATE".equals(type)) {
        String sessionId = String.valueOf(data.get("sessionId")).trim();
        if (currentItem != null && currentItem.getId().equals(sessionId)) {
          Object valObj = data.get("value");
          if (valObj instanceof Number num) {
            controller.updateRemainingTime(num.intValue());
          }
        }
      } else if ("END_AUCTION".equals(type)) {
        String sessionId = String.valueOf(data.get("sessionId")).trim();
        if (currentItem != null && currentItem.getId().equals(sessionId)) {
          String winner = (String) data.get("winnerName");
          controller.handleAuctionEndEvent(winner);
        }
      }
    }
  }
}
