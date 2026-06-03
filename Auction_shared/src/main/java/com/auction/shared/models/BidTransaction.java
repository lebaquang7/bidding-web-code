package com.auction.shared.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidTransaction implements Serializable {
  private static final long serialVersionUID = 1L;

  private String itemId;
  private String bidderId;
  private BigDecimal bidAmount;
  private LocalDateTime bidTime;
  private String bidderName;

  // Constructor dùng khi Client gửi yêu cầu đặt giá (Request)
  public BidTransaction(String itemId, String bidderId, BigDecimal bidAmount) {
    this.itemId = itemId;
    this.bidderId = bidderId;
    this.bidAmount = bidAmount;
    this.bidTime = LocalDateTime.now();
    this.bidderName = "";
  }

  // Constructor đầy đủ (Dùng khi lấy dữ liệu lịch sử từ DB lên)
  public BidTransaction(
      String itemId, String bidderId, BigDecimal bidAmount, LocalDateTime bidTime) {
    this.itemId = itemId;
    this.bidderId = bidderId;
    this.bidAmount = bidAmount;
    this.bidTime = bidTime;
  }

  // Getters
  public String getItemId() {
    return itemId;
  }

  public String getBidderId() {
    return bidderId;
  }

  public BigDecimal getBidAmount() {
    return bidAmount;
  }

  public LocalDateTime getBidTime() {
    return bidTime;
  }

  public String getBidderName() {
    return bidderName;
  }

  public void setBidderName(String bidderName) {
    this.bidderName = bidderName;
  }
}
