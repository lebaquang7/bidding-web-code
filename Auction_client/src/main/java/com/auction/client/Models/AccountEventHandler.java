package com.auction.client.Models;

import java.util.HashMap;

public class AccountEventHandler {
    // Temporary hashmap representation of account storage/database, need works...? 
    // eventually will fwd account info to server, retrieve from server instead?
    private static HashMap<String, String> accountStorage = new HashMap<>();
    
    /**
     * Static block that always run when class is loaded. Used to load accountStorage from a config file (for now)
     * Add extra comments if this is extended or adjusted.
     */
    static {
        // Temporary account login validation testing
        accountStorage.put("admin", "admin");
    }

    /**
     * Usage: validate account with name and pwd. Subject to change.
     * @param name
     * @param password
     * @return
     */
    
    public static String validateAccount(String name, String password){
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
