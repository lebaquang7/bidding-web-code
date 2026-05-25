package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.client.Models.TestChartData;
import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuctionViewController implements SceneController.ItemLoadable{
    @FXML Label auctionViewItemName;
    @FXML Label auctionViewStartingBid;
    @FXML Label auctionViewCurrentBid;
    @FXML Label auctionViewRemainingTime;
    @FXML Label auctionViewPlaceBidErrorBox;
    @FXML Label auctionViewAutoBidderErrorBox;

    @FXML TextField auctionViewPlaceBidBox;
    @FXML TextField auctionViewAutoBidderMaxBidBox;
    @FXML TextField auctionViewAutoBidderBidIncrementBox;

    @FXML LineChart<Number,Number> auctionViewPriceChart;
    @FXML NumberAxis auctionViewPriceChartXAxis;
    @FXML NumberAxis auctionViewPriceChartYAxis;
    
    private Item currentItem;
    
    public void auctionViewGoBackToList(ActionEvent event){
        SceneController.closeScene(event);
    }

    //TODO: work on these
    public void auctionViewPlaceBid(ActionEvent event){}
    public void auctionViewEnableAutoBid(ActionEvent event){}
    public void auctionViewStopAutoBid(ActionEvent event){}

    @Override
    public void setItem(Item item){
        this.currentItem=item;

        //init labels
        auctionViewItemName.setText(currentItem.getItemName());
        LabelHandler.setDetailedTooltip(auctionViewItemName);

        CurrencySelectorHandler.bindPriceLabel(auctionViewStartingBid, currentItem.getStartingPrice());
        LabelHandler.scaleFontSizeToFit(auctionViewStartingBid, 20, 12, 8, 1);

        CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, currentItem.getCurrentPrice());
        LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);

        //init chart
        //TODO: link chart with actual infos.
        auctionViewPriceChart.setTitle("Auction price for Item "+item.getItemName());
        auctionViewPriceChartXAxis.setLabel("Time");
        auctionViewPriceChartYAxis.setLabel("Price");

        //toggle off auto ranging so one can manually set bounds. bounds will be linked with observable to track price.
        auctionViewPriceChartXAxis.setAutoRanging(false);
        auctionViewPriceChartYAxis.setAutoRanging(false);

        //TODO: track based on time
        auctionViewPriceChartXAxis.setLowerBound(0);
        auctionViewPriceChartXAxis.setUpperBound(150);

        //TODO: add observer for price. also to do this with other infos that dynamically changes.
        auctionViewPriceChartYAxis.setLowerBound(item.getStartingPrice().doubleValue());
        auctionViewPriceChartYAxis.setUpperBound(item.getCurrentPrice().doubleValue());

        //TODO: tick mark auto adjust based on lower and upper bound
        auctionViewPriceChartYAxis.setTickUnit(500000);

        auctionViewPriceChart.setData(TestChartData.getSalesData());
    }
}
