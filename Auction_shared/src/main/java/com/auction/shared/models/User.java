package com.auction.shared.models;

import java.io.Serializable;

public abstract class User extends Entity implements Serializable {
    private String username;
    private String password;
    private String email;

    private static final long serialVersionUID = 1L;

    // Để email là null để đỡ phải sửa nhiều hàm khởi tạo, về sau để tự setEmail
    public User(String username, String password, String id) {
        super(id);
        this.username = username;
        this.password = password;
        this.email = null;
    }

    // Constructor rỗng để parse
    public User() {}

    public String getUsername() {
        return username;
    }
    public void setUsername(String userName) { this.username = userName; }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
