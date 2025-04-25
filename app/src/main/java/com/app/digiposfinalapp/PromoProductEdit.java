package com.app.digiposfinalapp;
public   class PromoProductEdit {
    private String barcode;
    private double price;
    private double costPrice;
    private String description;
    private String itemCode;
    private String pul;


    // Getters and setters
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getPul() {
        return pul;
    }

    public void setPul(String pul) {
        this.pul = pul;
    }
}