package com.auction.shared.models;

import java.io.Serializable;

public abstract class Entity implements Serializable{
    private String id;

    public Entity(String id) {
    }

    public String getId(){
        return id ;
    }
    public void setId(String id){
        this.id = id;
    }
}
