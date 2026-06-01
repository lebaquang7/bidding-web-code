package com.auction.client.utils;

import com.auction.shared.models.BidTransaction;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartDataHandler {
  public static ObservableList<XYChart.Series<Number, Number>> setChartDisplay(
      List<BidTransaction> bidHistory) {
    XYChart.Series<Number, Number> itemPriceChart = new XYChart.Series<>();
    for (BidTransaction bidTransaction : bidHistory) {
      // get epochtime for display in chart
      long epochTime =
          bidTransaction
              .getBidTime()
              .atZone(java.time.ZoneId.systemDefault())
              .toInstant()
              .getEpochSecond();
      itemPriceChart
          .getData()
          .add(new XYChart.Data<>(epochTime, bidTransaction.getBidAmount().doubleValue()));
    }
    ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
    data.add(itemPriceChart);
    return data;
  }
}
