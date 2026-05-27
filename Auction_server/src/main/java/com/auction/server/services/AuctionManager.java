package com.auction.server.services;

import com.auction.shared.models.Item;
import java.util.Map;

public class AuctionManager {

  private Map<String, Object> activeAuctions;
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

  public void startNewAuction(String auctionId, Item item) {
    // Code logic tạo phiên đấu giá và nhét vào activeAuctions
    System.out.println("Bắt đầu phiên đấu giá cho mặt hàng: " + item.getItemName());
  }

  private java.util.Map<String, java.util.Map<String, com.auction.shared.models.BidTransaction>>
      autoBidRegistry = new java.util.concurrent.ConcurrentHashMap<>();

  // TODO: proper auto bidding. subject for removal?
  // public void registerAutoBid(String auctionId, com.auction.shared.models.BidTransaction config)
  // {
  //     autoBidRegistry.putIfAbsent(auctionId, new java.util.concurrent.ConcurrentHashMap<>());
  //     autoBidRegistry.get(auctionId).put(config.getTempUsername(), config);
  //     System.out.println(" [Server] Đã bật Auto-Bid cho User: " + config.getTempUsername());
  // }

  // public void runAutoBiddingEngine(String auctionId, Object auctionObj) {
  //     java.util.Map<String, com.auction.shared.models.BidTransaction> bots =
  // autoBidRegistry.get(auctionId);
  //     if (bots == null || bots.isEmpty()) return;

  //     boolean priceChanged;
  //     BigDecimal currentPrice = BigDecimal.valueOf(5000);
  //     String currentHighestUser = "";

  //     if (auctionObj instanceof com.auction.shared.models.Auction) {
  //         currentPrice = ((com.auction.shared.models.Auction) auctionObj).getCurrentPrice();
  //     }

  //     System.out.println(" KÍCH HOẠT TỰ ĐỘNG ĐÈ GIÁ ");
  //     do {
  //         priceChanged = false;

  //         // Duyệt qua tất cả Client đăng ký Auto-Bid cho món đồ này
  //         for (com.auction.shared.models.BidTransaction bot : bots.values()) {
  //             String botUsername = bot.getTempUsername();
  //             if (botUsername == null) continue;

  //             // Nếu bot này chưa phải là người giữ giá cao nhất hiện tại
  //             if (!botUsername.equals(currentHighestUser)) {
  //                 double nextPossibleBid = currentPrice + bot.getIncrement();

  //                 // Nếu mức giá nhảy tiếp theo vẫn nằm trong ngân sách (maxBid) của Bot
  //                 if (nextPossibleBid <= bot.getMaxBid()) {
  //                     currentPrice = nextPossibleBid;
  //                     currentHighestUser = botUsername;
  //                     priceChanged = true;

  //                     System.out.println(" [Hệ thống] Bot [" + botUsername + "] tự động nâng giá
  // lên: " + currentPrice + " VND");

  //                     if (auctionObj instanceof com.auction.shared.models.Auction) {
  //                         ((com.auction.shared.models.Auction)
  // auctionObj).setCurrentPrice(currentPrice);
  //                     }
  //                 }
  //             }
  //         }
  //     } while (priceChanged); // Vòng lặp chạy liên tục cho đến khi không còn Bot nào đủ điều
  // kiện đè giá nữa

  //     System.out.println(" AUTO-BID KẾT THÚC. NGƯỜI DẪN ĐẦU: [" + currentHighestUser + "] VỚI
  // GIÁ: " + currentPrice + " VND");
  // }
}
