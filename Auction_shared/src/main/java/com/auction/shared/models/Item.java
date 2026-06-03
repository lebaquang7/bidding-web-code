package com.auction.shared.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class Item extends Entity {
  private String name;
  private String description;
  private final BigDecimal startingPrice;
  private transient ObjectProperty<BigDecimal> currentPrice = new SimpleObjectProperty<>();
  private String sellerId;
  private String highestBidderId;
  private BigDecimal priceIncrement;
  private String imagePath; // Lưu tên file ảnh
  private byte[] imageBytes; // Truyền dữ liệu ảnh qua Socket
  private int durationTime; // Phút
  private LocalDateTime endTime;
  private LocalDateTime startTime;
  private String highestBidderName;

  private static final long serialVersionUID = 1L;

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
    if (startTime != null && this.durationTime > 0 && this.endTime == null) {
      this.endTime = startTime.plusMinutes(this.durationTime);
    }
  }

  public Item(String name, String description, BigDecimal startingPrice, BigDecimal currentPrice) {
    super();
    this.name = name;
    this.currentPrice = new SimpleObjectProperty<>(currentPrice);
    this.startingPrice = startingPrice;
    this.description = description;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public byte[] getImageBytes() {
    return imageBytes;
  }

  public void setImageBytes(byte[] imageBytes) {
    this.imageBytes = imageBytes;
  }

  public String getItemName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getStartingPrice() {
    return startingPrice;
  }

  // Hàm Getter cho currentPrice (Dùng để Bind UI ở Client)
  public ObjectProperty<BigDecimal> currentPriceProperty() {
    return currentPrice;
  }

  // Getter/Setter thông thường
  public BigDecimal getCurrentPrice() {
    return currentPrice.get();
  }

  public void setCurrentPrice(BigDecimal price) {
    this.currentPrice.set(price);
  }

  public String getSellerId() {
    return sellerId;
  }

  public void setSellerId(String id) {
    this.sellerId = id;
  }

  public String getHighestBidderId() {
    return highestBidderId;
  }

  public void setHighestBidderId(String highestBidderId) {
    this.highestBidderId = highestBidderId;
  }

  public String getHighestBidderName() {
    return highestBidderName;
  }

  public void setHighestBidderName(String highestBidderName) {
    this.highestBidderName = highestBidderName;
  }

  public BigDecimal getPriceIncrement() {
    return priceIncrement;
  }

  public void setPriceIncrement(BigDecimal priceIncrement) {
    this.priceIncrement = priceIncrement;
  }

  public int getDurationTime() {
    return durationTime;
  }

  public void setDurationTime(int durationTime) {
    this.durationTime = durationTime;
  }

  // Xử lý việc Property curentPrice không Serializable được
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(getCurrentPrice()); // Ghi giá trị BigDecimal thực tế
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    BigDecimal price = (BigDecimal) in.readObject();
    this.currentPrice = new SimpleObjectProperty<>(price); // Khởi tạo lại Property sau khi nhận
  }
}
