package com.auction.client.Controllers;

import java.io.IOException;
import java.util.List;

import com.auction.client.Models.ItemsEventHandler;
import com.auction.shared.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.scene.layout.GridPane;

public class MainMenuAdminPaneController { 
    //mostly similar to auctionview controller but for admins to initialize auctions
    @FXML GridPane mainMenuAdminPaneGridPane;
    @FXML Pagination mainMenuAdminPanePagination;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    private static final int ITEMS_PER_PAGE = 6;
    private static final int COLUMN_COUNT = 2;

    public void initialize() {
        refreshItems();

        mainMenuAdminPanePagination.currentPageIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            renderItem(itemList, newIndex.intValue());
        });

        itemList.addListener((ListChangeListener<Item>) change -> {
            updatePagination();
            renderItem(itemList, mainMenuAdminPanePagination.getCurrentPageIndex());
        });
    }

    public void refreshItems() {
        List<Item> fetchedItems = ItemsEventHandler.fetchAllItems();

        if (fetchedItems != null) {
            itemList.setAll(fetchedItems);
        } else {
            itemList.clear();
        }

        updatePagination();
        renderItem(itemList, 0);
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) itemList.size() / ITEMS_PER_PAGE);
        mainMenuAdminPanePagination.setPageCount(pageCount == 0 ? 1 : pageCount);
    }

    public void renderItem(List<Item> list, int paginationIndex) {
        mainMenuAdminPaneGridPane.getChildren().clear();

        if (list.isEmpty()) return;

        int startIndex = paginationIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, list.size());

        if (startIndex >= list.size()) return;

        List<Item> itemListSublist = list.subList(startIndex, endIndex);

        for (int i = 0; i < itemListSublist.size(); i++) {
            try {
                Item item = itemListSublist.get(i);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/views/mainMenu_adminAuctionCard.fxml"));

                Parent card = loader.load();

                AdminCardController controller = loader.getController();
                controller.setData(item);

                int columnIndex = i / COLUMN_COUNT;
                int rowIndex = i % COLUMN_COUNT;

                mainMenuAdminPaneGridPane.add(card, columnIndex, rowIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}