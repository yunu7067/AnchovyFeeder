package com.example.anchovyfeeder;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FoodObject extends RealmObject {
    @PrimaryKey private String FOOD_CD;
    private String FOOD_NAME;
    private String FOOD_CLASS;
    private Long AMOUNT_PER_SERVINGS;
    private String UNIT;
    private Double KCAL;
    private Double PROTEIN;
    private Double FAT;
    private Double CARBOHYDRATE;

    public String getFOOD_CD() { return FOOD_CD; }
    public void setFOOD_CD(String FOOD_CD) { this.FOOD_CD = FOOD_CD; }

    public String getFOOD_NAME() { return FOOD_NAME; }
    public void setFOOD_NAME(String FOOD_NAME) { this.FOOD_NAME = FOOD_NAME; }

    public String getFOOD_CLASS() { return FOOD_CLASS; }
    public void setFOOD_CLASS(String FOOD_CLASS) { this.FOOD_CLASS = FOOD_CLASS; }

    public Long getAMOUNT_PER_SERVINGS() { return AMOUNT_PER_SERVINGS; }
    public void setAMOUNT_PER_SERVINGS(Long AMOUNT_PER_SERVINGS) { this.AMOUNT_PER_SERVINGS = AMOUNT_PER_SERVINGS; }

    public String getUNIT() { return UNIT; }
    public void setUNIT(String UNIT) { this.UNIT = UNIT; }

    public Double getKCAL() { return KCAL; }
    public void setKCAL(Double KCAL) { this.KCAL = KCAL; }

    public Double getPROTEIN() { return PROTEIN; }
    public void setPROTEIN(Double PROTEIN) { this.PROTEIN = PROTEIN; }

    public Double getFAT() { return FAT; }
    public void setFAT(Double FAT) { this.FAT = FAT; }

    public Double getCARBOHYDRATE() { return CARBOHYDRATE; }
    public void setCARBOHYDRATE(Double CARBOHYDRATE) { this.CARBOHYDRATE = CARBOHYDRATE; }

}
