package com.auction.client.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.auction.shared.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.scene.layout.GridPane;

public class MainMenuAuctionListPaneController {
    @FXML GridPane mainMenuAuctionListGridPane;
    @FXML Pagination mainMenuAuctionListPagination;
    ObservableList<Item> itemList = FXCollections.observableArrayList();
    //no. of items displayed per gridpane page and columns of grid
    private static final int ITEMS_PER_PAGE = 6;
    private static final int COLUMN_COUNT = 2;

    public void initialize(){
        //Placeholder item
        //TODO: wait for other's works on this, and then link it with their item list
        itemList.add(new Item("item", "desc", BigDecimal.valueOf(500000.0), BigDecimal.valueOf(9000000.0)) {
        });
        itemList.add(new Item("item2", "desc", BigDecimal.valueOf(200000.0), BigDecimal.valueOf(6000000.0)) {
        });
        itemList.add(new Item("item3", "desc", BigDecimal.valueOf(100000.0), BigDecimal.valueOf(300000.0)) {
        });

        //calc page count
        int pageCount = (int) Math.ceil(itemList.size() / ITEMS_PER_PAGE);
        //safety checkfor 0 page count
        mainMenuAuctionListPagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        //initial set
        mainMenuAuctionListPagination.setCurrentPageIndex(0);
        renderItem(itemList, 0);

        //listen to change in page pagination
        mainMenuAuctionListPagination.currentPageIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            renderItem(itemList, newIndex.intValue());
        });
        //listen to change in list eles
        itemList.addListener((ListChangeListener<Item>) change -> {
            renderItem(itemList, mainMenuAuctionListPagination.getCurrentPageIndex());
        });
    }

    /**
     * render list of pane items
     * @param list
     */
    public void renderItem(List<Item> list, int paginationIndex){
        //clear old panes
        mainMenuAuctionListGridPane.getChildren().clear();
        //calc index of list items to display
        int startIndex = paginationIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(paginationIndex * ITEMS_PER_PAGE + ITEMS_PER_PAGE, list.size());

        List<Item> itemListSublist = list.subList(startIndex, endIndex);
        for (int i = 0; i < itemListSublist.size(); i++) {
            try {
                Item item = itemListSublist.get(i);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/views/mainMenu_auctionCard.fxml"));
              
                //load data of the card
                Parent card = loader.load();
                //get loader controller, set data to each item's data
                AuctionCardController controller = loader.getController();
                controller.setData(item);

                int columnIndex = i/COLUMN_COUNT;
                int rowIndex = i%COLUMN_COUNT;
                mainMenuAuctionListGridPane.add(card, columnIndex, rowIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
