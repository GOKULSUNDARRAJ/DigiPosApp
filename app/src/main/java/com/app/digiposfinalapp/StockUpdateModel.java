package com.app.digiposfinalapp;

public class StockUpdateModel {
    private int id;
    private String barcode;
    private int PLU;
    private String date;
    private String time;
    private int currentQty;
    private int updateQty;
    private int doneBy;

    public StockUpdateModel(int id, String barcode, int PLU, String date, String time, int currentQty, int updateQty, int doneBy) {
        this.id = id;
        this.barcode = barcode;
        this.PLU = PLU;
        this.date = date;
        this.time = time;
        this.currentQty = currentQty;
        this.updateQty = updateQty;
        this.doneBy = doneBy;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getPLU() {
        return PLU;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getCurrentQty() {
        return currentQty;
    }

    public int getUpdateQty() {
        return updateQty;
    }

    public int getDoneBy() {
        return doneBy;
    }
}
