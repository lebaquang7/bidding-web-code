package com.auction.server.services;

import com.auction.server.DatabaseConfig;
import com.auction.shared.models.Auction;
import com.auction.shared.models.AuctionStatus;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Bidder;
import com.auction.shared.models.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuctionSession {
  private final String sessionId;
  private final Item auctionItem;
  private Bidder highestBidder;
  private Auction auctionDetails;
  private AuctionStatus currentState;
  private final List<BidTransaction> bidHistory;
  private final long durationInSeconds;
  private long remainingSeconds;
  private ScheduledFuture<?> countdownTask;

  private final Object lock = new Object();
  private final ScheduledExecutorService scheduler =
      Executors
          .newSingleThreadScheduledExecutor(); // Dùng để quản lý thời gian của phiên đấu giá và

  public void setCurrentState(AuctionStatus currentState) {
    this.currentState = currentState;
  }

  // thời gian chờ thanh toán

  public AuctionSession(
      String sessionId, Item auctionItem, Auction auctionDetails, long durationInSeconds) {
    this.sessionId = sessionId;
    this.auctionItem = auctionItem;
    this.auctionDetails = auctionDetails;
    this.durationInSeconds = durationInSeconds;
    this.bidHistory = new ArrayList<>();
    this.currentState = AuctionStatus.PENDING_APPROVAL;
  }

  public boolean startAuction() {
    synchronized (lock) {
      if (currentState != AuctionStatus.PENDING_APPROVAL) {
        return false;
      }

      boolean dbUpdated = DatabaseConfig.updateAuctionStatus(this.sessionId, AuctionStatus.RUNNING);

      if (dbUpdated) {
        this.remainingSeconds = durationInSeconds;
        this.currentState = AuctionStatus.RUNNING;

        if (auctionDetails != null) {
          auctionDetails.setStatus(currentState);
        }

        countdownTask =
            scheduler.scheduleAtFixedRate(
                () -> {
                  if (remainingSeconds > 0) {
                    remainingSeconds--;

                    Map<String, Object> timeData = new HashMap<>();
                    timeData.put("type", "TIME_UPDATE");
                    timeData.put(
                        "sessionId", sessionId); // Để Client biết đây là thời gian của phiên nào
                    timeData.put("value", remainingSeconds);
                    NotificationService.broadcast(timeData);
                    System.out.println("Gửi thời gian còn lại: " + remainingSeconds);
                  } else {
                    if (countdownTask != null) {
                      countdownTask.cancel(false);
                    }
                    endAuction();
                  }
                },
                1,
                1,
                TimeUnit.SECONDS);
        System.out.println("[Phiên " + sessionId + "] Đã bắt đầu countdown.");

        System.out.println("[Phiên " + sessionId + "] Đã mở bán thành công.");
        return true;
      } else {
        System.err.println("[Lỗi] Không tìm thấy Item ID: " + sessionId + " trong DB.");
        return false;
      }
    }
  }

  /**
   * Khi hết thời gian đấu giá, nếu có người thắng thì sẽ chuyển trạng thái sang FINISHED và thông
   * báo cho tất cả người tham gia. Nếu không có người thắng nào, thì sẽ chuyển trạng thái sang
   * CANCELLED và thông báo cho tất cả người tham gia.
   */
  public boolean endAuction() {
    synchronized (lock) {
      if (countdownTask != null) {
        countdownTask.cancel(false);
      }

      this.currentState = AuctionStatus.FINISHED;
      DatabaseConfig.updateAuctionStatus(sessionId, currentState);
      this.highestBidder = DatabaseConfig.getWinnerFromHistory(sessionId);

      if (highestBidder == null) {
        this.currentState = AuctionStatus.CANCELLED;
        System.out.println(
            "[Phiên đấu giá "
                + sessionId
                + "]: đã kết thúc mà không có người thắng. Mặt hàng: "
                + auctionItem.getItemName()
                + " sẽ được rút khỏi đấu giá.");
      }

      System.out.println("[Phiên đấu giá " + sessionId + "]: đã kết thúc.");
      System.out.println(
          "Người thắng: "
              + highestBidder.getUserName()
              + " với giá: "
              + auctionItem.getCurrentPrice());

      Map<String, Object> endData = new HashMap<>();
      endData.put("type", "END_AUCTION");
      endData.put("sessionId", sessionId);
      endData.put("winnerName", highestBidder.getUserName());
      NotificationService.broadcast(endData);

      scheduler.schedule(this::handledPaymentTimeout, 10, TimeUnit.MINUTES);
      return true;
    }
  }

  /**
   * Nếu người thắng không thanh toán trong thời gian quy định, hoặc nếu phiên đấu giá bị hủy bỏ bởi
   * admin, thì sẽ chuyển trạng thái sang CANCELLED và thông báo cho tất cả người tham gia.
   */
  public boolean cancle() {
    synchronized (lock) {
      if (this.currentState != AuctionStatus.RUNNING
          && this.currentState != AuctionStatus.FINISHED) {
        return false;
      }

      this.currentState = AuctionStatus.CANCELLED;
      System.out.println("[Phiên đấu giá " + sessionId + "]: đã bị hủy bỏ.");
      scheduler.shutdown();
      return true;
    }
  }

  /**
   * Sau khi phiên đấu giá kết thúc, người thắng sẽ có một khoảng thời gian nhất định (10 phút) để
   * thực hiện thanh toán. Nếu người thắng thanh toán thành công, thì sẽ chuyển trạng thái sang PAID
   * và thông báo cho tất cả người tham gia. Nếu người thắng không thanh toán trong thời gian quy
   * định, thì sẽ hủy bỏ phiên đấu giá và thông báo cho tất cả người tham gia.
   */
  public boolean markAsPaid() {
    synchronized (lock) {
      if (currentState != AuctionStatus.FINISHED) {
        return false;
      }

      PaymentService paymentService = PaymentService.getInstance();
      boolean isSuccess =
          paymentService.processPayment(highestBidder, auctionItem.getCurrentPrice());

      if (isSuccess) {
        this.currentState = AuctionStatus.PAID;
        System.out.println(" [Phiên " + sessionId + "] Đã nhận thanh toán. Giao dịch thành công!");
        scheduler.shutdown();
        return true;
      }

      System.out.println(
          " [Phiên "
              + sessionId
              + "] Thanh toán thất bại. Người thắng "
              + highestBidder.getUserName()
              + " không đủ tiền để thanh toán.");
      return false;
    }
  }

  /**
   * Hàm này sẽ được gọi khi hết thời gian chờ thanh toán sau khi phiên đấu giá kết thúc. Nếu người
   * thắng không thanh toán, thì sẽ hủy bỏ phiên đấu giá và thông báo cho tất cả người tham gia.
   */
  public void handledPaymentTimeout() {
    synchronized (lock) {
      if (this.currentState == AuctionStatus.FINISHED) {
        this.currentState = AuctionStatus.CANCELLED;
        System.out.println(
            "[Phiên đấu giá "
                + sessionId
                + "]: đã kết thúc nhưng người thắng "
                + highestBidder.getUserName()
                + " không thanh toán trong thời gian quy định. Phiên đấu giá bị hủy bỏ.");
        scheduler.shutdown();
      }
    }
  }

  public String getSessionId() {
    return sessionId;
  }

  public Item getAuctionItem() {
    return auctionItem;
  }

  public Bidder getHighestBidder() {
    return highestBidder;
  }

  public AuctionStatus getCurrentState() {
    return currentState;
  }

  public List<BidTransaction> getBidHistory() {
    return new ArrayList<>(bidHistory);
  }
}
