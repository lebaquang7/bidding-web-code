package com.auction.client.Models;

import com.auction.shared.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class TestChartData {
    //TODO: subject for removal. currently used as placeholder data to pricechart.
    public static ObservableList<XYChart.Series<Number, Number>> getSalesData(Item currentItem){
        XYChart.Series<Number, Number> itemPrice = new XYChart.Series<>();
        itemPrice.setName("PLACEHOLDER DATA - TO BE REPLACED");
        itemPrice.getData().addAll(new XYChart.Data<>(0, CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getStartingPrice()).doubleValue()));
        itemPrice.getData().addAll(new XYChart.Data<>(30, CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice()).doubleValue()/4));
        itemPrice.getData().addAll(new XYChart.Data<>(60, CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice()).doubleValue()/3));
        itemPrice.getData().addAll(new XYChart.Data<>(90,CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice()).doubleValue()/2));
        itemPrice.getData().addAll(new XYChart.Data<>(120,CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice()).doubleValue()/1.5));
        itemPrice.getData().addAll(new XYChart.Data<>(150,CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice()).doubleValue()));

        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        data.add(itemPrice);
        return data;
    }
}
