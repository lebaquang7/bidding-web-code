package com.auction.shared.models;

import java.math.BigDecimal;

public class Bidder extends User {
  private BigDecimal balance;
  private String shippingAddress;
  private int reputationScore; // đc tính sau những lần đấu giá

  private static final long serialVersionUID = 1L;

  public Bidder(
      String userName,
      String passWord,
      String shippingAddress,
      BigDecimal balance,
      int reputationScore) {
    super(userName, passWord);
    this.balance = BigDecimal.ZERO;
    this.shippingAddress = shippingAddress;
    this.reputationScore = reputationScore;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public String getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(String shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public int getReputationScore() {
    return reputationScore;
  }
}
