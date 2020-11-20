package com.example.anchovyfeeder.realmdb;

import androidx.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class CalorieObject extends RealmObject {
    @Index
    private Date DATE;
    private Long FOOD_NO;

    public void set(Date DATE, Long FOOD_NO, Double CALORIE, Double PROTEIN, Double FAT, Double CARBOHYDRATE) {
        this.DATE = DATE;
        this.FOOD_NO = FOOD_NO;
    }

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public Long getFOOD_NO() {
        return FOOD_NO;
    }

    public void setFOOD_NO(Long FOOD_NO) {
        this.FOOD_NO = FOOD_NO;
    }

}
