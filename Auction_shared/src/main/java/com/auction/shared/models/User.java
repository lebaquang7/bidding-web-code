package com.auction.shared.models;

public abstract class User extends Entity {
    private String userName;
    private String password;
    private String email;

    private static final long serialVersionUID = 1L;

    public User(String userName, String password, String email, String id) {
        super(id);
        this.userName = userName;
        this.password = password;
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
        return password;
    }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
