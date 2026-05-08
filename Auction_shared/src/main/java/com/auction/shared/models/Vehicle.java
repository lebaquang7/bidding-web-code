package com.auction.shared.models;

public class Vehicle extends Item{
    private String licensePlate;
    private int mileage;
    private int manufacturingYear;

    public Vehicle(String name, String description, double startingPrice, String sellerId){
        super(name, description, startingPrice, sellerId);
    }

    public Vehicle(){
        super();
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getMileage() {
        return mileage;
    }
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getManufacturingYear() {
        return manufacturingYear;
    }
    public void setManufacturingYear(int manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

}
