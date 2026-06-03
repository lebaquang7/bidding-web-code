package com.auction.server.services;

import com.auction.shared.models.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {

  private Map<String, AuctionSession> activeAuctions = new ConcurrentHashMap<>();
  private static volatile AuctionManager instance;

  private AuctionManager() {}

  public static AuctionManager getInstance() {
    AuctionManager result = instance;
    if (result == null) {
      synchronized (AuctionManager.class) {
        result = instance;
        if (result == null) {
          instance = result = new AuctionManager();
        }
      }
    }
    return result;
  }

  public AuctionSession getAuctionSession(String itemId) {
    return activeAuctions.get(itemId);
  }

  public void registerSession(String itemId, AuctionSession session) {
    activeAuctions.put(itemId, session);
  }

  public void startNewAuction(String auctionId, Item item) {
    // Code logic tạo phiên đấu giá và nhét vào activeAuctions
    System.out.println("Bắt đầu phiên đấu giá cho mặt hàng: " + item.getItemName());
  }

  public void applyAntiSniping(String itemId) {
    try {
      com.auction.shared.models.Item item = com.auction.server.DatabaseConfig.getItemById(itemId);
      if (item == null || item.getEndTime() == null) {
        return;
      }

      long now = System.currentTimeMillis();
      long triggerZone = 30 * 1000;
      long extensionTime = 60 * 1000;

      long endTimeMillis =
          item.getEndTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
      long timeRemaining = endTimeMillis - now;

      if (timeRemaining > 0 && timeRemaining <= triggerZone) {
        long newEndTimeMillis = endTimeMillis + extensionTime;

        java.time.LocalDateTime newEndTime =
            java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(newEndTimeMillis), java.time.ZoneId.systemDefault());
        item.setEndTime(newEndTime);

        System.out.println("[Anti-Sniping] Gia hạn 1 phút cho: " + itemId);

        com.auction.server.DatabaseConfig.updateItemEndTime(itemId, newEndTime);
        com.auction.server.services.NotificationService.broadcast(item);
      }
    } catch (Exception e) {
      System.err.println("Lỗi chạy Anti-sniping: " + e.getMessage());
    }
  }
}
