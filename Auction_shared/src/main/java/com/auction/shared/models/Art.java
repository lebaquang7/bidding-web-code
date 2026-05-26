package com.auction.shared.models;

import java.math.BigDecimal;

public class Art extends Item{
    private String artistName;
    private boolean isOriginal;
    private int creationYear;
    private String medium;

    public Art(String name, String description, BigDecimal startingPrice, BigDecimal currentPrice, String artistName, boolean isOriginal, int creationYear, String medium){
        super(name, description, startingPrice, currentPrice);
        this.artistName = artistName;
        this.creationYear = creationYear;
        this.isOriginal = isOriginal;
        this.medium = medium;
    }

    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public boolean getIsOriginal() {
        return isOriginal;
    }
    public void setIsOriginal(boolean isOriginal) {
        this.isOriginal = isOriginal;
    }

    public int getCreationYear() {
        return creationYear;
    }
    public void setCreationYear(int creationYear) {
        this.creationYear = creationYear;
    }

    public String getMedium() {
        return medium;
    }
    public void setMedium(String medium) {
        this.medium = medium;
    }
}
