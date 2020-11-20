package com.example.anchovyfeeder;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anchovyfeeder.realmdb.FoodObject;
import com.example.anchovyfeeder.realmdb.WeightObject;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.RealmResults;

public class MainViewModel extends ViewModel {
    static MutableLiveData<ArrayList<AlarmListItem>> alarmList = new MutableLiveData<>();
    static MutableLiveData<ArrayList<Entry>> calEntry = new MutableLiveData<>();
    static MutableLiveData<ArrayList<Entry>> weightEntry = new MutableLiveData<>();

    // Realm
    static public RealmResults<WeightObject> weightsThisMonth;
    static public RealmResults<FoodObject> foodsRealm;


    private void loadUsers() {

    }


    public static void setAlarmList(ArrayList<AlarmListItem> list) {
        Collections.sort(
                list,
                (item1, item2) -> {
                    return item1.getCalendar().compareTo(item2.getCalendar());
                });
        alarmList.setValue(list);
    }

}
