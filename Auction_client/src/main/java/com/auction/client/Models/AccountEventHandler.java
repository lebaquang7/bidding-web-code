package com.auction.client.Models;

import java.util.HashMap;

public class AccountEventHandler {
    // Temporary hashmap representation of account storage/database, need works...? 
    // eventually will fwd account info to server, retrieve from server instead?
    private static HashMap<String, String> accountStorage = new HashMap<>();
    
    public static String validateAccount(String name, String password){
        // Temporary account login validation testing
        accountStorage.put("admin", "adminPassword");
        if (accountStorage.containsKey(name)){ //do name exists in database
            if (accountStorage.get(name).equals(password)){ //do password align with password field?
                return "loginSuccessful";
            } else { //if password doesnt align with password field:
                return "invalidPassword";
            } 
        } else { //if name doesnt exist in database
            return "accountDoesntExist";
        }
    }
}
