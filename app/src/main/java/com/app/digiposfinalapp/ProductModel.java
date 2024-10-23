package com.app.digiposfinalapp;

public class ProductModel {
    private int id;
    private String description;
    private double price;
    private String barcode;

    // Constructor, getters and setters
    public ProductModel(int id, String description, double price, String barcode) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.barcode = barcode;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getBarcode() {
        return barcode;
    }
}
