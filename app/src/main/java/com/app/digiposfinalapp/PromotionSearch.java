package com.app.digiposfinalapp;
public class PromotionSearch {
    private int id;
    private int promoID;
    private String description;
    private String receipt;
    private String ruleNo;
    private int ruleValue;
    private String type;
    private String typeValue;
    private String startDate;
    private String endDate;
    private int itemCount;
    private String plu;
    private boolean done;
    private String promoName;
    private String dealType;
    private String promoTarget;
    private Integer maxUses;
    private String status;
    private double unitPrice;

    public PromotionSearch(int id, int promoID, String description, String receipt,
                           String ruleNo, int ruleValue, String type, String typeValue,
                           String startDate, String endDate, int itemCount, String plu,
                           boolean done, String promoName, String dealType, String promoTarget,
                           Integer maxUses, String status, double unitPrice) {
        this.id = id;
        this.promoID = promoID;
        this.description = description;
        this.receipt = receipt;
        this.ruleNo = ruleNo;
        this.ruleValue = ruleValue;
        this.type = type;
        this.typeValue = typeValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.itemCount = itemCount;
        this.plu = plu;
        this.done = done;
        this.promoName = promoName;
        this.dealType = dealType;
        this.promoTarget = promoTarget;
        this.maxUses = maxUses;
        this.status = status;
        this.unitPrice = unitPrice;
    }

    // Getters
    public int getId() { return id; }
    public int getPromoID() { return promoID; }
    public String getDescription() { return description; }
    public String getReceipt() { return receipt; }
    public String getRuleNo() { return ruleNo; }
    public int getRuleValue() { return ruleValue; }
    public String getType() { return type; }
    public String getTypeValue() { return typeValue; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getItemCount() { return itemCount; }
    public String getPlu() { return plu; }
    public boolean isDone() { return done; }
    public String getPromoName() { return promoName; }
    public String getDealType() { return dealType; }
    public String getPromoTarget() { return promoTarget; }
    public Integer getMaxUses() { return maxUses; }
    public String getStatus() { return status; }
    public double getUnitPrice() { return unitPrice; }
}