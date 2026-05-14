package com.auction.shared.models;

public class Electronics extends Item{
    private int warrantyMonths;
    private String brand;
    private String model;
    private String condition;

    public Electronics(String name, String description, double startingPrice, double currentPrice, String id) {
        super(name, description, startingPrice, currentPrice, id);
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }
    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String module) {
        this.model = model;
    }

    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }

}
