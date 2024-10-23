package com.app.digiposfinalapp;

public class SupplierModel {
    private int id;
    private String supplier;
    private int done;

    public SupplierModel() {

    }

    public SupplierModel(int id, String supplier, int done) {
        this.id = id;
        this.supplier = supplier;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
