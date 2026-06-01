package com.auction.client.utils;

public class MiscTools {
  // round up numbers to nearest rounded of 1-2-5 series
  public static double roundUp(double number) {
    if (number <= 0)
      return 0;

    // find order of magnitude with logarithm
    double magnitude = Math.pow(10, Math.floor(Math.log10(number)));

    double normalized = number / magnitude;

    // 1-2-5 rounding
    double roundedNormalized;
    if (normalized <= 1.0) {
      roundedNormalized = 1.0;
    } else if (normalized <= 2.0) {
      roundedNormalized = 2.0;
    } else if (normalized <= 5.0) {
      roundedNormalized = 5.0;
    } else {
      roundedNormalized = 10.0;
    }

    return roundedNormalized * magnitude;
  }

  /**
   * Usage: format time in second, to minutes
   */
  public static String formatSecondsToMinutes(int totalSeconds) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return String.format("%02d:%02d", minutes, seconds);
  }
}
