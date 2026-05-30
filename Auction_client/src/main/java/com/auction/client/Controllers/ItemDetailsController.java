package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;
import java.io.ByteArrayInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemDetailsController implements SceneController.ItemLoadable {

  @FXML Label itemDetailsID;
  @FXML Label itemDetailsItemName;
  @FXML Label itemDetailsDescription;
  @FXML Label itemDetailsInitialPrice;
  @FXML Label itemDetailsCurrentPrice;
  @FXML Label itemDetailsBidsMade;
  @FXML Label itemDetailsBidders;
  @FXML Label itemDetailsLastBid;
  @FXML Label itemDetailsTimeRemaining;
  @FXML ImageView itemDetailsImageView;

  private Item currentItem;

  public void itemDetailsGoBackToList(ActionEvent event) {
    SceneController.closeScene(event);
  }

  // TODO: have to track based on real time price changes. same with other item
  // labels that actively
  // changes
  @Override
  public void setItem(Item item) {
    this.currentItem = item;

    itemDetailsID.setText(currentItem.getId());
    LabelHandler.setDetailedTooltip(itemDetailsID);

    itemDetailsItemName.setText(currentItem.getItemName());
    LabelHandler.setDetailedTooltip(itemDetailsItemName);

    itemDetailsDescription.setText(currentItem.getDescription());
    LabelHandler.setDetailedTooltip(itemDetailsDescription);

    CurrencySelectorHandler.bindPriceLabel(itemDetailsInitialPrice, currentItem.getStartingPrice());
    LabelHandler.scaleFontSizeToFit(itemDetailsInitialPrice, 15, 12, 10, 1);

    CurrencySelectorHandler.bindPriceLabel(itemDetailsCurrentPrice, currentItem.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(itemDetailsCurrentPrice, 15, 12, 10, 1);

    if (currentItem.getImageBytes() != null) {
      itemDetailsImageView.setImage(
          new Image(new ByteArrayInputStream(currentItem.getImageBytes())));
    } else if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
      new Thread(
              () -> {
                byte[] bytes = ItemsEventHandler.downloadItemImage(currentItem.getImagePath());
                if (bytes != null) {
                  currentItem.setImageBytes(bytes);
                  Platform.runLater(
                      () -> {
                        itemDetailsImageView.setImage(new Image(new ByteArrayInputStream(bytes)));
                      });
                }
              })
          .start();
    }

    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(
                  () -> {
                    if (newVal != null) {
                      CurrencySelectorHandler.bindPriceLabel(itemDetailsCurrentPrice, newVal);
                      LabelHandler.scaleFontSizeToFit(itemDetailsCurrentPrice, 20, 12, 10, 1);
                    }
                  });
            });
  }
}
