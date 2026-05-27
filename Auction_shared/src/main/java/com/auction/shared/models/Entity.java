package com.auction.shared.models;

import java.io.Serializable;
import java.util.UUID;

public abstract class Entity implements Serializable {
  private String id;

  public Entity() {
    this.id = UUID.randomUUID().toString(); // Khởi tạo Id
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
