package com.auction.shared.models;

public abstract class User extends Entity {
  private String userName;
  private String passWord;

  private static final long serialVersionUID = 1L;

  public User(String userName, String passWord) {
    super();
    this.userName = userName;
    this.passWord = passWord;
  }

  public User() {
    super();
  } // Constructor rỗng cho việc parse dữ liệu sau này

  public String getUserName() {
    return userName;
  }

  public void setUsername(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return passWord;
  }

  public void setPassword(String password) {
    this.passWord = password;
  }
}
