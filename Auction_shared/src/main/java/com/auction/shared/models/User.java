package com.auction.shared.models;

public abstract class User extends Entity {
    private String userName;
    private String passWord;
    private String email;

    private static final long serialVersionUID = 1L;

    public User(String userName, String passWord, String email) {
        super();
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
    }

    public User() {
        super();
    } // Constructor rỗng cho việc parse dữ liệu sau này

    public String getUserName() {
        return userName;
    }
    public void setUsername(String userName) { this.userName = userName; }

    public String getPassword() {
        return passWord;
    }
    public void setPassword(String password) { this.passWord = password; }

    // nhắn tin cho email về cuộc đấu giá đã tham gia và đấu giá thành công
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
