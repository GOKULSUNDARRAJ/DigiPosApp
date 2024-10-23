package com.app.digiposfinalapp;

import java.io.Serializable;

public class Status implements Serializable {

    private String name;
    private int image;

    public Status() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}