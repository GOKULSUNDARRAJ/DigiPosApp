package com.app.digiposfinalapp;

public class ChillerSpinner {
    private int id;
    private String chillerName;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChillerName() {
        return chillerName;
    }

    public void setChillerName(String chillerName) {
        this.chillerName = chillerName;
    }

    @Override
    public String toString() {
        return chillerName;
    }
}