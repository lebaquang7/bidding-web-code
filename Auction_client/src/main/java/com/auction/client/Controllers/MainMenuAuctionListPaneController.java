package com.auction.client.Controllers;

import com.auction.client.Models.ItemsEventHandler;
import com.auction.shared.models.Item;
import java.io.IOException;
import java.util.List;
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

  private ObservableList<Item> itemList = FXCollections.observableArrayList();

  // Số lượng vật phẩm hiển thị trên mỗi trang và số cột của lưới
  private static final int ITEMS_PER_PAGE = 6;
  private static final int COLUMN_COUNT = 2;

  public void initialize() {
    // 1. Lấy dữ liệu thật từ Server thay vì placeholder
    refreshItems();

    // 2. Lắng nghe thay đổi của trang hiện tại trên Pagination
    mainMenuAuctionListPagination
        .currentPageIndexProperty()
        .addListener(
            (observable, oldIndex, newIndex) -> {
              renderItem(itemList, newIndex.intValue());
            });
    // java.util.ArrayList<Item> serverItems =
    // com.auction.client.Models.AccountEventHandler.getAllItems();
    // itemList.setAll(serverItems);

    // 3. Lắng nghe thay đổi của danh sách phần tử (nếu có cập nhật danh sách)
    itemList.addListener(
        (ListChangeListener<Item>)
            change -> {
              updatePagination();
              renderItem(itemList, mainMenuAuctionListPagination.getCurrentPageIndex());
            });
  }

  /** Gọi Server để lấy danh sách vật phẩm mới nhất và cập nhật UI */
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

  /** Cập nhật số lượng trang dựa trên kích thước danh sách thực tế */
  private void updatePagination() {
    // Sử dụng (double) để tránh lỗi chia số nguyên (Integer Division)
    int pageCount = (int) Math.ceil((double) itemList.size() / ITEMS_PER_PAGE);
    mainMenuAuctionListPagination.setPageCount(pageCount == 0 ? 1 : pageCount);
  }

  /** Hiển thị danh sách vật phẩm lên GridPane dựa trên chỉ mục trang */
  public void renderItem(List<Item> list, int paginationIndex) {
    // Xóa các card cũ trên giao diện
    mainMenuAuctionListGridPane.getChildren().clear();

    if (list.isEmpty()) return;

    // Tính toán khoảng vật phẩm cần hiển thị cho trang hiện tại
    int startIndex = paginationIndex * ITEMS_PER_PAGE;
    int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, list.size());

    if (startIndex >= list.size()) return;

    List<Item> itemListSublist = list.subList(startIndex, endIndex);

    for (int i = 0; i < itemListSublist.size(); i++) {
      try {
        Item item = itemListSublist.get(i);
        FXMLLoader loader =
            new FXMLLoader(
                getClass().getResource("/com/auction/client/views/mainMenu_auctionCard.fxml"));

        Parent card = loader.load();

        // Lấy controller của card và truyền dữ liệu Item vào
        AuctionCardController controller = loader.getController();
        controller.setData(item);

        // Tính toán vị trí cột và dòng (2 cột, nhiều dòng)
        int columnIndex = i / COLUMN_COUNT;
        int rowIndex = i % COLUMN_COUNT;

        mainMenuAuctionListGridPane.add(card, columnIndex, rowIndex);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
