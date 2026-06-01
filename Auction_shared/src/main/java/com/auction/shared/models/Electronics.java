package com.auction.shared.models;

import java.math.BigDecimal;

public class Electronics extends Item {
  private int warrantyMonths;
  private String brand;
  private String model;
  private String condition;

  public Electronics(
      String name,
      String description,
      BigDecimal startingPrice,
      BigDecimal currentPrice,
      int warrantyMonths,
      String brand,
      String model,
      String condition) {
    super(name, description, startingPrice, currentPrice);
    this.warrantyMonths = warrantyMonths;
    this.brand = brand;
    this.model = model;
    this.condition = condition;
  }

  public int getWarrantyMonths() {
    return warrantyMonths;
  }

  public void setWarrantyMonths(int warrantyMonths) {
    this.warrantyMonths = warrantyMonths;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String module) {
    this.model = model;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }
}
