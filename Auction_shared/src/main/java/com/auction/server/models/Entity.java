package com.auction.server.models;

import java.io.Serializable;

public abstract class Entity implements Serializable{
    protected int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
