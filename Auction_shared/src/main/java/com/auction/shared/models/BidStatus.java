package com.auction.shared.models;

public class BidStatus {
  public enum bidStatus {
    SUCCESS,
    INVALID,
    ALREADY_HIGHEST,
    NOT_STARTED
  }
}
