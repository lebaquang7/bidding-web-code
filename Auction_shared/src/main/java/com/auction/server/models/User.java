package com.auction.server.models;

import java.io.Serializable;

public abstract class User extends Entity implements Serializable {
    private String username;
    private String password;
    private String email;

    private static final long serialVersionUID = 1L;


    public User(String userName, String passWord, int id) {
        super(id);
        this.username = userName;
        this.password = passWord;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {return email;}

    public void setUserName(String userName) { this.username = userName; }

    //Thêm điều kiện cho setPassword?
    public void setPassWord(String passWord) { this.password = passWord; }

    public void setEmail(String email) { this.email = email; }
}
