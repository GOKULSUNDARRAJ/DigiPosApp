package com.app.digiposfinalapp;
public class Product {
    private String id;
    private String plu;
    private String barcode;
    private String description;
    private String subDepartment;
    private String supplier;
    private double buyPrice;
    private int quantity;
    private String department;
    private double saleWithVat;
    private double discount;
    private double costPerCase;
    private double price;
    private double vat;
    private double margin;
    private String ageLimit;
    private String proImage;
    private int promoID;
    private int unitPerCase;
    private boolean activated;
    private String dateAdded;
    private String vatValue;
    private String productClass;
    private int qtySold;
    private int capacity;
    private boolean done;
    private double price2;
    private int ssQtys;
    private double ssPrice;
    private int ssPoints;
    private String ssProType;
    private double unitScale;
    private String itemCode;
    private String itemType;
    private String foodType;
    private String menuType;
    private String subMenuType;
    private int menuTypeNo;
    private int subProductNo;
    private String brand;
    private String expiryDate;
    private double profitIncVat;
    private double profitExVat;
    private double costIncVat;
    private double markup;
    private int num;
    private double costIncVatPerUnit;

    public Product(String id, String plu, String barcode, String description, String subDepartment, String supplier, double buyPrice, int quantity, String department, double saleWithVat, double discount, double costPerCase, double price, double vat, double margin, String ageLimit, String proImage, int promoID, int unitPerCase, boolean activated, String dateAdded, String vatValue, String productClass, int qtySold, int capacity, boolean done, double price2, int ssQtys, double ssPrice, int ssPoints, String ssProType, double unitScale, String itemCode, String itemType, String foodType, String menuType, String subMenuType, int menuTypeNo, int subProductNo, String brand, String expiryDate, double profitIncVat, double profitExVat, double costIncVat, double markup, int num, double costIncVatPerUnit) {
        this.id = id;
        this.plu = plu;
        this.barcode = barcode;
        this.description = description;
        this.subDepartment = subDepartment;
        this.supplier = supplier;
        this.buyPrice = buyPrice;
        this.quantity = quantity;
        this.department = department;
        this.saleWithVat = saleWithVat;
        this.discount = discount;
        this.costPerCase = costPerCase;
        this.price = price;
        this.vat = vat;
        this.margin = margin;
        this.ageLimit = ageLimit;
        this.proImage = proImage;
        this.promoID = promoID;
        this.unitPerCase = unitPerCase;
        this.activated = activated;
        this.dateAdded = dateAdded;
        this.vatValue = vatValue;
        this.productClass = productClass;
        this.qtySold = qtySold;
        this.capacity = capacity;
        this.done = done;
        this.price2 = price2;
        this.ssQtys = ssQtys;
        this.ssPrice = ssPrice;
        this.ssPoints = ssPoints;
        this.ssProType = ssProType;
        this.unitScale = unitScale;
        this.itemCode = itemCode;
        this.itemType = itemType;
        this.foodType = foodType;
        this.menuType = menuType;
        this.subMenuType = subMenuType;
        this.menuTypeNo = menuTypeNo;
        this.subProductNo = subProductNo;
        this.brand = brand;
        this.expiryDate = expiryDate;
        this.profitIncVat = profitIncVat;
        this.profitExVat = profitExVat;
        this.costIncVat = costIncVat;
        this.markup = markup;
        this.num = num;
        this.costIncVatPerUnit = costIncVatPerUnit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubDepartment() {
        return subDepartment;
    }

    public void setSubDepartment(String subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSaleWithVat() {
        return saleWithVat;
    }

    public void setSaleWithVat(double saleWithVat) {
        this.saleWithVat = saleWithVat;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getCostPerCase() {
        return costPerCase;
    }

    public void setCostPerCase(double costPerCase) {
        this.costPerCase = costPerCase;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public String getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(String ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getProImage() {
        return proImage;
    }

    public void setProImage(String proImage) {
        this.proImage = proImage;
    }

    public int getPromoID() {
        return promoID;
    }

    public void setPromoID(int promoID) {
        this.promoID = promoID;
    }

    public int getUnitPerCase() {
        return unitPerCase;
    }

    public void setUnitPerCase(int unitPerCase) {
        this.unitPerCase = unitPerCase;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getVatValue() {
        return vatValue;
    }

    public void setVatValue(String vatValue) {
        this.vatValue = vatValue;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public int getQtySold() {
        return qtySold;
    }

    public void setQtySold(int qtySold) {
        this.qtySold = qtySold;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public double getPrice2() {
        return price2;
    }

    public void setPrice2(double price2) {
        this.price2 = price2;
    }

    public int getSsQtys() {
        return ssQtys;
    }

    public void setSsQtys(int ssQtys) {
        this.ssQtys = ssQtys;
    }

    public double getSsPrice() {
        return ssPrice;
    }

    public void setSsPrice(double ssPrice) {
        this.ssPrice = ssPrice;
    }

    public int getSsPoints() {
        return ssPoints;
    }

    public void setSsPoints(int ssPoints) {
        this.ssPoints = ssPoints;
    }

    public String getSsProType() {
        return ssProType;
    }

    public void setSsProType(String ssProType) {
        this.ssProType = ssProType;
    }

    public double getUnitScale() {
        return unitScale;
    }

    public void setUnitScale(double unitScale) {
        this.unitScale = unitScale;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getSubMenuType() {
        return subMenuType;
    }

    public void setSubMenuType(String subMenuType) {
        this.subMenuType = subMenuType;
    }

    public int getMenuTypeNo() {
        return menuTypeNo;
    }

    public void setMenuTypeNo(int menuTypeNo) {
        this.menuTypeNo = menuTypeNo;
    }

    public int getSubProductNo() {
        return subProductNo;
    }

    public void setSubProductNo(int subProductNo) {
        this.subProductNo = subProductNo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getProfitIncVat() {
        return profitIncVat;
    }

    public void setProfitIncVat(double profitIncVat) {
        this.profitIncVat = profitIncVat;
    }

    public double getProfitExVat() {
        return profitExVat;
    }

    public void setProfitExVat(double profitExVat) {
        this.profitExVat = profitExVat;
    }

    public double getCostIncVat() {
        return costIncVat;
    }

    public void setCostIncVat(double costIncVat) {
        this.costIncVat = costIncVat;
    }

    public double getMarkup() {
        return markup;
    }

    public void setMarkup(double markup) {
        this.markup = markup;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getCostIncVatPerUnit() {
        return costIncVatPerUnit;
    }

    public void setCostIncVatPerUnit(double costIncVatPerUnit) {
        this.costIncVatPerUnit = costIncVatPerUnit;
    }
}
