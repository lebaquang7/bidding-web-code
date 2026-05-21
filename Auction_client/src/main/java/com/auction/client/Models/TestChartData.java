package com.auction.client.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class TestChartData {
    //TODO: subject for removal. currently used as placeholder data to pricechart.
    public static ObservableList<XYChart.Series<Number, Number>> getSalesData(){
        XYChart.Series<Number, Number> itemPrice = new XYChart.Series<>();
        itemPrice.setName("PLACEHOLDER DATA - TO BE REPLACED");
        itemPrice.getData().addAll(new XYChart.Data<>(0,500000));
        itemPrice.getData().addAll(new XYChart.Data<>(30,950000));
        itemPrice.getData().addAll(new XYChart.Data<>(60,3000000));
        itemPrice.getData().addAll(new XYChart.Data<>(90,3500000));
        itemPrice.getData().addAll(new XYChart.Data<>(120,5000000));
        itemPrice.getData().addAll(new XYChart.Data<>(150,8000000));

        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        data.add(itemPrice);
        return data;
    }
}
