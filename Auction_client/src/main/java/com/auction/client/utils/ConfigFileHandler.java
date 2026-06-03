package com.auction.client.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigFileHandler {
  // Class xử lý việc lưu thông tin persistent của client (cài đặt theme, tiền tệ) vào máy
  private static final String FOLDER_DIR_NAME = "Auction_Client";
  private static final String CONFIG_FILE_NAME = "config.txt";
  private static Path configFilePath;
  private static Properties properties = new Properties();

  // Khởi động đường dẫn, load
  static {
    String userHome = System.getProperty("user.home");
    configFilePath = Paths.get(userHome, FOLDER_DIR_NAME, CONFIG_FILE_NAME);
    load();
  }

  /**
   * Usage: Lưu cặp K-V cài đặt, dùng để lưu trữ cài đặt để dùng lại sau này
   *
   * @param key
   * @param value
   */
  public static void setProperty(String key, String value) {
    properties.setProperty(key, value);
    save();
  }

  /**
   * Usage: Lấy cặp K-V từ file cài đặt ra, dùng để lấy dữ liệu cài đặt
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public static String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  /** Usage: Lưu file */
  private static void save() {
    try {
      // Chắc chắn đường dẫn tồn tại trước khi viết
      Files.createDirectories(configFilePath.getParent());

      try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
        properties.store(writer, "UserConfigs");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Usage: Mở file và load */
  private static void load() {
    if (!Files.exists(configFilePath)) {
      return; // Nếu không có file, dừng lại
    }

    try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
      properties.load(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
