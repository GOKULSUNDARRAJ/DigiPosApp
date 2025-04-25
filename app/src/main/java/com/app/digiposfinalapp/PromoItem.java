package com.app.digiposfinalapp;

public class PromoItem {
    private int id;
    private String plu;
    private int promoId;
    private boolean done;
    private String barcode;  // Changed from long to String
    private double ddPrice;
    private double discountPrice;
    private double promotionPrice;

    public PromoItem(int id, String plu, int promoId, boolean done, String barcode, double ddPrice, double discountPrice, double promotionPrice) {
        this.id = id;
        this.plu = plu;
        this.promoId = promoId;
        this.done = done;
        this.barcode = barcode;
        this.ddPrice = ddPrice;
        this.discountPrice = discountPrice;
        this.promotionPrice = promotionPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    public int getPromoId() {
        return promoId;
    }

    public void setPromoId(int promoId) {
        this.promoId = promoId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getDdPrice() {
        return ddPrice;
    }

    public void setDdPrice(double ddPrice) {
        this.ddPrice = ddPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }
}