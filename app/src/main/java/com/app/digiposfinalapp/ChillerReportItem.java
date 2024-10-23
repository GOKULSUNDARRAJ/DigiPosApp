package com.app.digiposfinalapp;

public class ChillerReportItem {
    private int id;
    private String chillerName;
    private double temperature;
    private String date;
    private String time;
    private int changeId;
    private String day;

    public ChillerReportItem() {

    }

    public ChillerReportItem(int id, String chillerName, double temperature, String date, String time, int changeId, String day) {
        this.id = id;
        this.chillerName = chillerName;
        this.temperature = temperature;
        this.date = date;
        this.time = time;
        this.changeId = changeId;
        this.day = day;
    }
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
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

    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
