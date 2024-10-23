package com.app.digiposfinalapp;

public class DepartmentModel {
    private int departmentId;
    private String age;
    private String department;
    private int num;
    private int noshop;
    private int points;
    private int done;
    private String image;

    public DepartmentModel(int departmentId, String age, String department, int num, int noshop, int points, int done, String image) {
        this.departmentId = departmentId;
        this.age = age;
        this.department = department;
        this.num = num;
        this.noshop = noshop;
        this.points = points;
        this.done = done;
        this.image = image;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNoshop() {
        return noshop;
    }

    public void setNoshop(int noshop) {
        this.noshop = noshop;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
