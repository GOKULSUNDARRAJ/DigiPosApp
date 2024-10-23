package com.app.digiposfinalapp;

public class SubDepartmentspinner {
    private int id;
    private int autoID; // Add this field
    private String department;
    private String subDepartment; // New field
    private boolean done; // New field
    private String num; // New field

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getAutoID() { // Getter for AutoID
        return autoID;
    }
    public void setAutoID(int autoID) { // Setter for AutoID
        this.autoID = autoID;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getSubDepartment() { // Getter for Sub_Departments
        return subDepartment;
    }
    public void setSubDepartment(String subDepartment) { // Setter for Sub_Departments
        this.subDepartment = subDepartment;
    }
    public boolean isDone() { // Getter for done
        return done;
    }
    public void setDone(boolean done) { // Setter for done
        this.done = done;
    }
    public String getNum() { // Getter for Num
        return num;
    }
    public void setNum(String num) { // Setter for Num
        this.num = num;
    }
}
