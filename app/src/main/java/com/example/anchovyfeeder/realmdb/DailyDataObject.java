package com.example.anchovyfeeder.realmdb;

import android.net.Uri;

import com.github.mikephil.charting.data.Entry;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * 하루의 데이터를 저장하는 RealmObject
 * Date 객체로 구별하며, 인덱싱한다.
 * (Realm에서 Date를 PrimaryKey로 지정할 수 없기 때문에 기본키는 아님)
 */
public class DailyDataObject extends RealmObject {
    @Index
    private Date DATE;
    private RealmList<FoodObject> FOODS;
    private Double WEIGHT;

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public void setDATE(int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.DATE = cal.getTime();
    }

    public RealmList<FoodObject> getFOODS() {
        return FOODS;
    }

    public void setFOODS(RealmList<FoodObject> FOODS) {
        this.FOODS = FOODS;
    }

    public Float getWEIGHT() {
        return ((WEIGHT != null) ? WEIGHT.floatValue() : 0f);
    }

    public void setWEIGHT(Double WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public void setWEIGHT(Float WEIGHT) {
        this.WEIGHT = WEIGHT.doubleValue();
    }

    public void appendFOODS(RealmList<FoodObject> FOODS) {
        this.FOODS.addAll(FOODS);
    }

    /**
     * 칼로리 데이터 합계 반환
     *
     * @return FOODS.sum(" KCAL ").floatValue();
     */
    public float getCalorieSum() {
        return FOODS.sum("KCAL").floatValue();
    }

    /**
     * 단백질 데이터 합계 반환
     *
     * @return FOODS.sum(" PROTEIN ").floatValue();
     */
    public float getProteinSum() {
        return FOODS.sum("PROTEIN").floatValue();
    }

    /**
     * 지방 데이터 합계 반환
     *
     * @return FOODS.sum(" FAT ").floatValue();
     */
    public float getFatSum() {
        return FOODS.sum("FAT").floatValue();
    }

    /**
     * 탄수화물 데이터 합계 반환
     *
     * @return FOODS.sum(" CARBOHYDRATE ").floatValue();
     */
    public float getCarbohydrateSum() {
        return FOODS.sum("CARBOHYDRATE").floatValue();
    }

    public Entry getWeightEntry() {
        // 1. 날짜 계산
        Calendar cal = Calendar.getInstance();
        cal.setTime(DATE);
        int date = cal.get(Calendar.DATE);
        // 2. 몸무계 계산
        float weight = this.WEIGHT.floatValue();
        return new Entry(date, weight);
    }

    public Entry getCalorieEntry() {
        // 1. 날짜 계산
        Calendar cal = Calendar.getInstance();
        cal.setTime(DATE);
        int date = cal.get(Calendar.DATE);
        // 2. 칼로리 합계 계산
        float calories = this.getCalorieSum();
        return new Entry(date, calories);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyDataObject that = (DailyDataObject) o;
        return that.DATE.equals(this.DATE);
    }
}
