package com.example.anchovyfeeder.realmdb;

import androidx.annotation.Nullable;

import io.realm.RealmObject;

public class FoodObject extends RealmObject {
    private long NO;
    private String FOOD_NAME;
    private String FOOD_TYPE;
    private Double AMOUNT_PER_SERVINGS;
    private String UNIT;
    private Double KCAL;
    private Double PROTEIN;
    private Double FAT;
    private Double CARBOHYDRATE;

    public long getNO() {
        return NO;
    }

    public void setNO(long NO) {
        this.NO = NO;
    }

    public String getFOOD_NAME() {
        return FOOD_NAME;
    }

    public void setFOOD_NAME(String FOOD_NAME) {
        this.FOOD_NAME = FOOD_NAME;
    }

    public String getFOOD_TYPE() {
        return FOOD_TYPE;
    }

    public void setFOOD_TYPE(String FOOD_TYPE) {
        this.FOOD_TYPE = FOOD_TYPE;
    }

    public Double getAMOUNT_PER_SERVINGS() {
        return AMOUNT_PER_SERVINGS;
    }

    public void setAMOUNT_PER_SERVINGS(Double AMOUNT_PER_SERVINGS) {
        this.AMOUNT_PER_SERVINGS = AMOUNT_PER_SERVINGS;
    }

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String UNIT) {
        this.UNIT = UNIT;
    }

    public Double getKCAL() {
        return KCAL;
    }

    public void setKCAL(Double KCAL) {
        this.KCAL = KCAL;
    }

    public Double getPROTEIN() {
        return PROTEIN;
    }

    public void setPROTEIN(Double PROTEIN) {
        this.PROTEIN = PROTEIN;
    }

    public Double getFAT() {
        return FAT;
    }

    public void setFAT(Double FAT) {
        this.FAT = FAT;
    }

    public Double getCARBOHYDRATE() {
        return CARBOHYDRATE;
    }

    public void setCARBOHYDRATE(Double CARBOHYDRATE) {
        this.CARBOHYDRATE = CARBOHYDRATE;
    }


}
