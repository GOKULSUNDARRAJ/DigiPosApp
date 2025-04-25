package com.app.digiposfinalapp;
public class NewPromotion {
    private String description;
    private String barcode;
    private String itemCode;
    private String price;
    private String costPrice;
    private String promoID;
    private String plu;

    // Add other fields as needed from your database query

    public NewPromotion(String description, String barcode, String itemCode, String price, String costPrice, String promoID, String plu) {
        this.description = description;
        this.barcode = barcode;
        this.itemCode = itemCode;
        this.price = price;
        this.costPrice = costPrice;
        this.promoID = promoID;
        this.plu = plu;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getPromoID() {
        return promoID;
    }

    public void setPromoID(String promoID) {
        this.promoID = promoID;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }
}