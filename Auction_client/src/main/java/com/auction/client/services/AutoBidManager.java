package com.auction.client.services;

import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import java.math.BigDecimal;

public class AutoBidManager {
  // Class quản lý worker cho auto bid
  private static AutoBidWorker autoBidWorker;
  private static Thread workerThread;

  // Nested class để trả về trạng thái của auto bid
  public static class AutoBidState {
    // Thông báo cho UI về trạng thái của auto bid
    private final String message;
    private final String styleClass;

    public AutoBidState(String message, String styleClass) {
      this.message = message;
      this.styleClass = styleClass;
    }

    public String getMessage() {
      return message;
    }

    public String getStyleClass() {
      return styleClass;
    }
  }

  /**
   * Usage: Khởi động auto bid
   *
   * @param item Sản phẩm
   * @param user Người dùng
   * @param maxBidVND Bid tối đa
   * @param incrementVND Mức tăng mỗi lần bid
   * @return
   */
  public static AutoBidState startAutoBid(
      Item item, User user, BigDecimal maxBidVND, BigDecimal incrementVND) {
    BigDecimal minRequiredBid = AuctionBiddingService.calculateMinimumRequiredBid(item);
    BigDecimal currentPrice = item.getCurrentPrice();
    BigDecimal serverMinIncrement = minRequiredBid.subtract(currentPrice);

    String statusMessage = "Auto-Bid đã kích hoạt!";
    String style = "green";

    if (incrementVND.compareTo(serverMinIncrement) < 0) {
      statusMessage = "Bước giá thấp hơn sàn. Bot sẽ tự nâng lên mức tối thiểu.";
      style = "orange";
    }

    // Dừng các worker đang chạy sẵn
    stopAutoBid();

    autoBidWorker = new AutoBidWorker(item, user, maxBidVND, incrementVND);
    workerThread = new Thread(autoBidWorker);
    workerThread.setDaemon(true);
    workerThread.start();

    return new AutoBidState(statusMessage, style);
  }

  /** Usage: Dừng auto bid */
  public static void stopAutoBid() {
    if (autoBidWorker != null) {
      autoBidWorker.stop();
      autoBidWorker = null;
    }
    if (workerThread != null && workerThread.isAlive()) {
      workerThread.interrupt();
      workerThread = null;
    }
  }

  /** Usage: gọi worker */
  public static void wakeUpWorker() {
    if (autoBidWorker != null) {
      autoBidWorker.wakeUp();
    }
  }

  /**
   * Usage: trả về trạng thái của worker
   *
   * @return
   */
  public static boolean isBotActive() {
    return autoBidWorker != null;
  }
}
