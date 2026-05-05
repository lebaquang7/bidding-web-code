package com.auction.server.models;

public abstract class User extends Entity {
    private String userName;
    private String passWord;

    private static final long serialVersionUID = 1L;

    public User(String userName, String passWord, int id) {
        super(id);
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    //Có thể thêm setPassword
}
