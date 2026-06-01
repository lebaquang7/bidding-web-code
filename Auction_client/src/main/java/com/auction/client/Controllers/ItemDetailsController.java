package com.auction.client.controllers;

import java.io.ByteArrayInputStream;

import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.Item;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemDetailsController implements SceneHandler.ItemLoadable {

  @FXML
  Label itemDetailsID;
  @FXML
  Label itemDetailsItemName;
  @FXML
  Label itemDetailsDescription;
  @FXML
  Label itemDetailsInitialPrice;
  @FXML
  Label itemDetailsCurrentPrice;
  @FXML
  Label itemDetailsWinner;
  @FXML
  Label itemDetailsTimeRemaining;
  @FXML
  Label auctionWonLabel;
  @FXML
  Button auctionWonButton;
  @FXML
  ImageView itemDetailsImageView;

  private Item currentItem;

  public void itemDetailsGoBackToList(ActionEvent event) {
    SceneHandler.closeScene(event);
  }

  // TODO: opens a small panel where user can input details. does nothing with the
  // details given. removes the auction when info in small panel is given.
  public void proceedToPayment(ActionEvent event) {
    SceneHandler.closeScene(event);
  }

  public void initialize() {
    // TODO: Logic for hiding win label and button when auction havent been won and
    // display when auction is won for user
    // auctionWonLabel.setVisible(false);
    // auctionWonButton.setVisible(false);
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
