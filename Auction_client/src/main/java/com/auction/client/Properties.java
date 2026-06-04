package com.auction.client;

import java.math.BigDecimal;

public class Properties {
  // Thông tin sử dụng chung cho cả app
  // https://github.com/ZacharyDavidSaunders/InventoryManagementSystem/blob/master/src/InventoryManagementSystem/Properties.java

  private static final String APPLICATION_NAME = "Auction Client";
  private static final String APPLICATION_VERSION = "RELEASE v1.0.0";
  private static final String APPLICATION_NAME_AND_VERSION =
      APPLICATION_NAME + " (" + APPLICATION_VERSION + ")";

  private static final String APPLICATION_IMAGE_DIRECTORY = "/images/icon.png";

  public static String getAPPLICATION_NAME() {
    return APPLICATION_NAME;
  }

  public static String getAPPLICATION_VERSION() {
    return APPLICATION_VERSION;
  }

  public static String getAPPLICATION_NAME_AND_VERSION() {
    return APPLICATION_NAME_AND_VERSION;
  }

  public static String getAPPLICATION_IMAGE_DIRECTORY() {
    return APPLICATION_IMAGE_DIRECTORY;
  }

  // Thông tin tỉ số quy đổi đong vị tiền tệ, dùng ở CurrencySelector handler
  private static final BigDecimal USD_TO_VND_RATE = new BigDecimal("26300");

  public static BigDecimal getUSD_TO_VND_RATE() {
    return USD_TO_VND_RATE;
  }

  // Thông tin IP và Port của server
  private static final String SERVER_IP = "127.0.0.1";
  private static final int SERVER_PORT = 1234;

  public static String getSERVER_IP() {
    return SERVER_IP;
  }

  public static int getSERVER_PORT() {
    return SERVER_PORT;
  }
}
