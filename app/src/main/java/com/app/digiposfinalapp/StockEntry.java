package com.app.digiposfinalapp;

public class StockEntry {
    private long id; // New field
    private String barcode;
    private int plu;
    private String description;
    private String date;
    private String time;
    private String type;
    private String reason;
    private int currentStock;
    private int stockIn;
    private int stockOut;
    private String scaleType;
    private double currentPrice;
    private int tillNo;
    private String userName;
    private int logId;
    private String shortcutDescription;

    public StockEntry() {

    }

    public StockEntry(long id, String barcode, int plu, String description, String date, String time, String type, String reason, int currentStock, int stockIn, int stockOut, String scaleType, double currentPrice, int tillNo, String userName, int logId, String shortcutDescription) {
        this.id = id;
        this.barcode = barcode;
        this.plu = plu;
        this.description = description;
        this.date = date;
        this.time = time;
        this.type = type;
        this.reason = reason;
        this.currentStock = currentStock;
        this.stockIn = stockIn;
        this.stockOut = stockOut;
        this.scaleType = scaleType;
        this.currentPrice = currentPrice;
        this.tillNo = tillNo;
        this.userName = userName;
        this.logId = logId;
        this.shortcutDescription = shortcutDescription;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getPlu() {
        return plu;
    }

    public void setPlu(int plu) {
        this.plu = plu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getStockIn() {
        return stockIn;
    }

    public void setStockIn(int stockIn) {
        this.stockIn = stockIn;
    }

    public int getStockOut() {
        return stockOut;
    }

    public void setStockOut(int stockOut) {
        this.stockOut = stockOut;
    }

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getTillNo() {
        return tillNo;
    }

    public void setTillNo(int tillNo) {
        this.tillNo = tillNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getShortcutDescription() {
        return shortcutDescription;
    }

    public void setShortcutDescription(String shortcutDescription) {
        this.shortcutDescription = shortcutDescription;
    }
}
