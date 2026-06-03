package com.auction.client.services;

import com.auction.client.MainApp;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import java.math.BigDecimal;

public class AutoBidWorker implements Runnable {
  // Usage: Worker cho auto bid
  private final Item item;
  private final User user;
  private final BigDecimal maxBid;
  private final BigDecimal increment;
  private volatile boolean running = true;

  /**
   * Constructor
   *
   * @param item
   * @param user
   * @param maxBid
   * @param increment
   */
  public AutoBidWorker(Item item, User user, BigDecimal maxBid, BigDecimal increment) {
    this.item = item;
    this.user = user;
    this.maxBid = maxBid;
    this.increment = increment;
  }

  /** Dừng worker */
  public void stop() {
    running = false;
    wakeUp();
  }

  /** Gọi worker */
  public void wakeUp() {
    Object lock = MainApp.getNotificationListener();
    if (lock != null) {
      synchronized (lock) {
        lock.notifyAll();
      }
    }
  }

  /** Usage: Chạy khi worker được call */
  @Override
  public void run() {
    while (running) {
      Object lock = MainApp.getNotificationListener();
      if (lock != null) {
        synchronized (lock) {
          try {
            lock.wait(5000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
        if (!running) break;
      }

      // Kiểm tra nếu autobid thấp hơn giá tối thiểu thì chọn giá tối thiểu thay
      BigDecimal currentPrice = item.getCurrentPrice();
      BigDecimal minRequiredBid = AuctionBiddingService.calculateMinimumRequiredBid(item);
      BigDecimal userSuggestedBid = currentPrice.add(increment);
      BigDecimal finalBid = userSuggestedBid.max(minRequiredBid);

      if (userSuggestedBid.compareTo(minRequiredBid) < 0) {
        System.out.println(
            "Bước giá bạn chọn ("
                + increment
                + ") thấp hơn quy định. Bot tự động nâng lên mức tối thiểu: "
                + minRequiredBid.subtract(currentPrice));
      }

      // so sánh với maxbid
      boolean isNotHighestBidder =
          item.getHighestBidderId() == null || !item.getHighestBidderId().equals(user.getId());
      boolean isWithinBudget = finalBid.compareTo(maxBid) <= 0;

      if (isNotHighestBidder) {
        if (isWithinBudget) {
          System.out.println("Đã đặt giá: " + finalBid + " (Min required: " + minRequiredBid + ")");
          ItemsEventHandler.placeBid(item.getId(), user.getId(), finalBid);
        } else {
          System.out.println("Giá tối thiểu vượt quá ngân sách");
          stop();
        }
      }
    }
  }
}
