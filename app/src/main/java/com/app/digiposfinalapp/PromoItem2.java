package com.app.digiposfinalapp;

public class PromoItem2 {
    private int promoId;
    private String promoName;
    private String type;
    private String startDate;  // Changed from Date to String
    private String endDate;    // Changed from Date to String
    private String ruleValue;

    // Getters and Setters
    public int getPromoId() { return promoId; }
    public void setPromoId(int promoId) { this.promoId = promoId; }

    public String getPromoName() { return promoName; }
    public void setPromoName(String promoName) { this.promoName = promoName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStartDate() { return startDate; }  // Changed return type
    public void setStartDate(String startDate) { this.startDate = startDate; }  // Changed parameter type

    public String getEndDate() { return endDate; }      // Changed return type
    public void setEndDate(String endDate) { this.endDate = endDate; }          // Changed parameter type

    public String getRuleValue() { return ruleValue; }
    public void setRuleValue(String ruleValue) { this.ruleValue = ruleValue; }
}