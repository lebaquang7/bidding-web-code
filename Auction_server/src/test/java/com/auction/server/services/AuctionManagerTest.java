package com.auction.server.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.auction.server.DatabaseConfig;
import com.auction.shared.models.Item;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@DisplayName("Server Module: Kiểm thử Anti-Sniping - AuctionManager")
public class AuctionManagerTest {

  private MockedStatic<DatabaseConfig> mockedDbConfig;

  @BeforeEach
  void setUp() {
    mockedDbConfig = Mockito.mockStatic(DatabaseConfig.class);
  }

  @AfterEach
  void tearDown() {
    mockedDbConfig.close();
  }

  @Test
  @DisplayName("TC-SERVER-01: Kiểm tra khởi tạo Singleton của AuctionManager")
  void testSingletonInstance() {
    assertNotNull(
        AuctionManager.getInstance(), "AuctionManager phải được khởi tạo theo dạng Singleton");
  }

  @Test
  @DisplayName("TC-SERVER-02: Thuật toán Anti-Sniping gia hạn thời gian kết thúc thành công")
  void testApplyAntiSniping_TriggerExtension() {
    Item mockItem = Mockito.mock(Item.class);

    // Mô phỏng phiên đấu giá sắp kết thúc trong vòng 15 giây
    LocalDateTime dangerEndTime = LocalDateTime.now().plusSeconds(15);
    Mockito.when(mockItem.getEndTime()).thenReturn(dangerEndTime);

    mockedDbConfig.when(() -> DatabaseConfig.getItemById(Mockito.anyString())).thenReturn(mockItem);

    // Kích hoạt bộ kiểm tra ngầm Anti-Sniping
    AuctionManager.getInstance().applyAntiSniping("item_snipe");

    // Khẳng định hệ thống kích hoạt thành công cơ chế gia hạn thời gian
    Mockito.verify(mockItem, Mockito.atLeast(1)).setEndTime(Mockito.any());
  }
}
