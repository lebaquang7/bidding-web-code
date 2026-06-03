package com.auction.server.services;

import com.auction.server.DatabaseConfig;
import com.auction.shared.models.Item;

import java.time.LocalDateTime;
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

  public void applyAntiSniping(String itemId) {
    AuctionSession session = activeAuctions.get(itemId);

    if (session == null) return;

    try {
      Item item = DatabaseConfig.getItemById(itemId);
      if (item == null || item.getEndTime() == null) {
        return;
      }

      long triggerZone = 60;
      long extensionTime = 60;

      long timeRemaining = session.getRemainingSeconds();

      // Kiểm tra nếu còn dưới 1 phút kích hoạt
      if (timeRemaining > 0 && timeRemaining <= triggerZone) {
        // Gia hạn trong RAM (Session sẽ tự gửi TIME_UPDATE mới về Client)
        session.extendDuration(extensionTime);

        // Đồng bộ xuống Database để néu khởi tạo lại vẫn cập nhật được
        LocalDateTime newEndTime = session.getAuctionItem().getEndTime();
        DatabaseConfig.updateItemEndTime(itemId, newEndTime);

        System.out.println("[Anti-Sniping] Gia hạn thêm 1p cho " + itemId);
      }
    } catch (Exception e) {
      System.err.println("Lỗi chạy Anti-sniping: " + e.getMessage());
    }
  }
}
