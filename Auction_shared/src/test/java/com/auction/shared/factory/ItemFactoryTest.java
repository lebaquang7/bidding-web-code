package com.auction.shared.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.auction.shared.models.Art;
import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import com.auction.shared.models.Vehicle;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Shared Module: Kiểm thử quy chuẩn OOP & Factory Pattern")
public class ItemFactoryTest {

  @Test
  @DisplayName("TC-SHARED-01: Khởi tạo đa hình các loại vật phẩm qua Factory thành công")
  void testFactoryPatternMultiObjects() {
    ItemFactory artFactory = new ArtFactory();
    ItemFactory electronicsFactory = new ElectronicsFactory();
    ItemFactory vehicleFactory = new VehicleFactory();

    BigDecimal price = new BigDecimal("1000.00");

    Item artItem = artFactory.createItem("Tranh Đêm đầy sao", "Bản sao phục dựng", price, price);
    Item elecItem =
        electronicsFactory.createItem("iPhone 17 Pro Max", "Máy trần 99%", price, price);
    Item vehicleItem = vehicleFactory.createItem("Xe điện Vinfast", "Xe chính chủ", price, price);

    // Kiểm tra tính đóng gói và không null
    assertNotNull(artItem);
    assertNotNull(elecItem);
    assertNotNull(vehicleItem);

    // Xác minh tính kế thừa và đa hình (Polymorphism & Inheritance)
    assertTrue(artItem instanceof Art, "Vật phẩm sinh ra phải thuộc lớp con Art");
    assertTrue(elecItem instanceof Electronics, "Vật phẩm sinh ra phải thuộc lớp con Electronics");
    assertTrue(vehicleItem instanceof Vehicle, "Vật phẩm sinh ra phải thuộc lớp con Vehicle");
  }
}
