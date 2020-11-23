package com.example.anchovyfeeder.realmdb;

import androidx.annotation.Nullable;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class CalorieObject extends RealmObject {
    @Index
    private Date DATE;
    private RealmList<FoodObject> FOODOBJECT;

    public void set(Date DATE, Long FOOD_NO, Double CALORIE, Double PROTEIN, Double FAT, Double CARBOHYDRATE) {
        this.DATE = DATE;
    }

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }


    public RealmList<FoodObject> getFOODOBJECT() {
        return FOODOBJECT;
    }

    public void setFOODOBJECT(RealmList<FoodObject> FOODOBJECT) {
        this.FOODOBJECT = FOODOBJECT;
    }
}
