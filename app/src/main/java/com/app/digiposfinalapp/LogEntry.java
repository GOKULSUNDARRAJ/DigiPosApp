package com.app.digiposfinalapp;

public class LogEntry {
    private int id;
    private String logID;
    private String userID;
    private String transID;
    private String shiftStartTime;
    private String shiftEndTime;
    private String shiftDate;
    private String shiftEndDate;
    private int tillNo;
    private String states;
    private String username;
    private String done;

    // Constructor, getters, and setters
    public LogEntry(int id, String logID, String userID, String transID, String shiftStartTime, String shiftEndTime,
                    String shiftDate, String shiftEndDate, int tillNo, String states, String username, String done) {
        this.id = id;
        this.logID = logID;
        this.userID = userID;
        this.transID = transID;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
        this.shiftDate = shiftDate;
        this.shiftEndDate = shiftEndDate;
        this.tillNo = tillNo;
        this.states = states;
        this.username = username;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public String getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public String getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public String getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(String shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getShiftEndDate() {
        return shiftEndDate;
    }

    public void setShiftEndDate(String shiftEndDate) {
        this.shiftEndDate = shiftEndDate;
    }

    public int getTillNo() {
        return tillNo;
    }

    public void setTillNo(int tillNo) {
        this.tillNo = tillNo;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }
}
