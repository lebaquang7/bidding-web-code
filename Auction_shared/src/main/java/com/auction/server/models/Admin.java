package com.auction.server.models;

import java.io.Serializable;

//Singleton
public class Admin extends User implements Serializable{
    private static Admin instance;

    private static final long serialVersionUID = 1L;

    public Admin(String userName, String passWord, int id) {
        super(userName, passWord, id);
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin("Admin", "Admin@123", 1);
        }
        return instance;
    }
}
