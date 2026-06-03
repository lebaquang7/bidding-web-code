package com.auction.client.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;

public class ChartTimeLabelFormatter extends StringConverter<Number> {
  // Class định dạng dữ liệu thời gian cho biểu đồ
  private NumberAxis xAxis;

  // Định nghĩa cách hiện thời gian
  private final DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
  private final DateTimeFormatter longFormat = DateTimeFormatter.ofPattern("dd/MM HH:mm");

  /**
   * Usage: Đặt trục cho định dạng thời gian
   *
   * @param xAxis
   */
  public ChartTimeLabelFormatter(NumberAxis xAxis) {
    this.xAxis = xAxis;
  }

  /*
   * Usage: Dùng để định dạng thời gian ở dạng giây ra biểu đồ
   */
  @Override
  public String toString(Number object) {
    long epochSecond = object.longValue();

    LocalDateTime time =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault());

    double lowerBound = xAxis.getLowerBound();
    double upperBound = xAxis.getUpperBound();
    double timeRange = upperBound - lowerBound;

    // nếu thời gian trong khoảng biểu đồ lớn hơn 1 ngày
    if (timeRange > 86400) {
      return time.format(longFormat); // display dd/MM hh:mm
    } else {
      return time.format(shortFormat); // display hh:mm:ss
    }
  }

  /** Không dùng đến, chỉ implement cho yêu cầu implement của StringConverter */
  @Override
  public Number fromString(String string) {
    return 0;
  }
}
