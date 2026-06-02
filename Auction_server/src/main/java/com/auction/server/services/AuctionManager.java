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

  private Map<String, Map<String, com.auction.shared.models.BidTransaction>> autoBidRegistry =
      new java.util.concurrent.ConcurrentHashMap<>();

  public static class AutoBidConfig {
    public String itemId;
    public String bidderId;
    public java.math.BigDecimal maxBid;
    public java.math.BigDecimal increment;

    public AutoBidConfig(
        String itemId,
        String bidderId,
        java.math.BigDecimal maxBid,
        java.math.BigDecimal increment) {
      this.itemId = itemId;
      this.bidderId = bidderId;
      this.maxBid = maxBid;
      this.increment = increment;
    }
  }

  private final Map<String, List<AutoBidConfig>> activeAutoBids = new ConcurrentHashMap<>();

  public void registerAutoBid(
      String itemId, String bidderId, BigDecimal maxBid, BigDecimal increment) {
    activeAutoBids
        .computeIfAbsent(itemId, k -> new java.util.concurrent.CopyOnWriteArrayList<>())
        .add(new AutoBidConfig(itemId, bidderId, maxBid, increment));
    System.out.println(
        "[Hệ thống] Đã cài đặt Bot Auto-Bid cho user:" + bidderId + " | Item: " + itemId);
  }

  // Engine tự động quét và đấu giá đè nhau
  public void triggerAutoBid(String itemId) {
    java.util.List<AutoBidConfig> configs = activeAutoBids.get(itemId);
    if (configs == null || configs.isEmpty()) return;

    boolean priceChanged;
    do {
      priceChanged = false;
      // Lấy dữ liệu mới nhất từ DB
      com.auction.shared.models.Item item = com.auction.server.DatabaseConfig.getItemById(itemId);
      if (item == null) return;

      for (AutoBidConfig bot : configs) {
        // Tránh việc Bot tự trả giá đè lên chính mình
        if (bot.bidderId.equals(item.getHighestBidderId())) continue;
        java.math.BigDecimal nextBid = item.getCurrentPrice().add(bot.increment);

        if (nextBid.compareTo(bot.maxBid) <= 0) {
          com.auction.shared.models.BidStatus.bidStatus status =
              com.auction.server.services.BiddingService.placeBid(itemId, bot.bidderId, nextBid);

          if (status == com.auction.shared.models.BidStatus.bidStatus.SUCCESS) {
            priceChanged = true;
            System.out.println(
                "[Auto-Bid] Bot [" + bot.bidderId + "] tự động nâng giá lên: " + nextBid);

            com.auction.shared.models.BidTransaction botBidNotification =
                new com.auction.shared.models.BidTransaction(itemId, bot.bidderId, nextBid);
            com.auction.server.services.NotificationService.broadcast(botBidNotification);

            break;
          }
        }
      }
    } while (priceChanged); // Lặp lại cho đến khi không còn Bot nào trả giá nữa
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
