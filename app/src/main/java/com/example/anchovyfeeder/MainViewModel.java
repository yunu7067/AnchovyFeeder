package com.example.anchovyfeeder;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anchovyfeeder.realmdb.DailyDataObject;
import com.example.anchovyfeeder.realmdb.FoodObject;
import com.example.anchovyfeeder.realmdb.PhotoObject;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.RealmResults;

public class MainViewModel extends ViewModel {
    static MutableLiveData<ArrayList<AlarmListItem>> alarmList = new MutableLiveData<>();
    // Realm Object
    //static public RealmResults<FoodObject> foodsRealm;
    static public ArrayList<FoodObject> foodList;
    static public RealmResults<DailyDataObject> DailyDatas;
    static public RealmResults<PhotoObject> Photos;

    public static void setAlarmList(ArrayList<AlarmListItem> list) {
        Collections.sort(list, (item1, item2) -> {
            return item1.getCalendar().compareTo(item2.getCalendar());
        });
        alarmList.setValue(list);
    }

}
