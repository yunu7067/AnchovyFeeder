package com.example.anchovyfeeder.realmdb;

import com.github.mikephil.charting.data.Entry;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class WeightObject extends RealmObject {
    @Index
    private Date DATE;
    private Double WEIGHT;

    public void set(int year, int month, int date, Double weight) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.DATE = cal.getTime();
        this.WEIGHT = weight;
    }

    public Double getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(Double WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public Date getDATE() {
        return DATE;
    }


    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public Entry getEntry() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DATE);
        int date = cal.get(Calendar.DATE);
        float weight = this.WEIGHT.floatValue();
        return new Entry(date, weight);
    }
}
