// Lớp dùng để phân loại yêu cầu đăng nhập, đăng ký, trả giá
package com.auction.shared.models;

import java.io.Serializable;

public class NetworkRequest implements Serializable {
  public enum requestType {
    Login,
    Bid,
    Register,
    SellItem,
    GetAllItems,
    SubscribeNotification,
    GetItemImage,
    FetchBidHistory,
    InitializeAuction
  }

  private requestType type;
  private Object data;

  public NetworkRequest(requestType type, Object data) {
    this.type = type;
    this.data = data;
  }

  public requestType getType() {
    return type;
  }

  public Object getData() {
    return data;
  }
}
