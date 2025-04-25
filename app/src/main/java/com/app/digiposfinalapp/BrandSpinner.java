package com.app.digiposfinalapp;

public class BrandSpinner {
    private int id;
    private String brand;
    private int done;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    // Override toString() to return the department name
    @Override
    public String toString() {
        return brand; // Return the department name
    }
}
