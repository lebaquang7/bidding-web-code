package com.auction.shared.models;

public class BidStatus {
  public enum bidStatus {
    SUCCESS,
    WINNER,
    INVALID,
    ALREADY_HIGHEST,
    EXPIRED,
    NOT_STARTED
  }
}
