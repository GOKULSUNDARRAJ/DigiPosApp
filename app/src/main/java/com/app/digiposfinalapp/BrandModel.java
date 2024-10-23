package com.app.digiposfinalapp;

public class BrandModel {
    private int id;
    private String brandName;
    private int done;

    public BrandModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public BrandModel(int id, String brandName, int done) {
        this.id = id;
        this.brandName = brandName;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}



