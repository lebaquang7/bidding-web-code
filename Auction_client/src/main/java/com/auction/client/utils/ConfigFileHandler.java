package com.auction.client.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigFileHandler {
  private static final String FOLDER_DIR_NAME = "Auction_Client";
  private static final String CONFIG_FILE_NAME = "config.txt";
  private static Path configFilePath;
  private static Properties properties = new Properties();

  // initialize directory and load
  static {
    String userHome = System.getProperty("user.home");
    configFilePath = Paths.get(userHome, FOLDER_DIR_NAME, CONFIG_FILE_NAME);
    load();
  }

  /**
   * Usage: store a properties key, value pair, for storing persistent configurations
   *
   * @param key
   * @param value
   */
  public static void setProperty(String key, String value) {
    properties.setProperty(key, value);
    save();
  }

  /**
   * Usage: get a properties k/v pair, for loading persistent configs
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public static String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  private static void save() {
    try {
      // makes sure directory exists before writing
      Files.createDirectories(configFilePath.getParent());

      try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
        properties.store(writer, "UserConfigs");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void load() {
    if (!Files.exists(configFilePath)) {
      return; // if no file, doesnt execute further
    }

    try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
      properties.load(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
