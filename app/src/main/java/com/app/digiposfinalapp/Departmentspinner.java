package com.app.digiposfinalapp;

public class Departmentspinner {
    private int id;
    private int age; // We will use this for the spinner
    private String department;
    private String num;
    private String noshop;
    private int points;
    private boolean done;
    private String image;
    private double vat;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDepartment() {  // Getter for department
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNoshop() {
        return noshop;
    }

    public void setNoshop(String noshop) {
        this.noshop = noshop;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }
}
