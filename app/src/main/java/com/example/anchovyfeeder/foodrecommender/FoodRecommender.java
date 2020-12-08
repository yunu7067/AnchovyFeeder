package com.example.anchovyfeeder.foodrecommender;

import android.util.Log;

import com.example.anchovyfeeder.AlarmListItem;
import com.example.anchovyfeeder.MainViewModel;
import com.example.anchovyfeeder.realmdb.DailyDataObject;
import com.example.anchovyfeeder.realmdb.FoodObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmResults;

public class FoodRecommender {
    final Double AdultManRecommendedCalories = 2700.0;

    public ArrayList<FoodObject> getRecommendedFood() {
        ArrayList<FoodObject> recommends = new ArrayList<FoodObject>();

        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.MILLISECOND, -1);
        Date date2 = cal.getTime();
        DailyDataObject daily = MainViewModel.DailyDatas.where().between("DATE", date, date2).findFirst();

        if (daily != null) {
            Double dayCaloie = (double) daily.getCalorieSum();

            RealmResults<AlarmListItem> results = MainViewModel.DailyDatas.getRealm().where(AlarmListItem.class).findAll();
            ArrayList<AlarmListItem> alarmList = new ArrayList<AlarmListItem>();
            alarmList.addAll(MainViewModel.DailyDatas.getRealm().copyFromRealm(results));

            int count = 0;
            for (AlarmListItem item : alarmList) {
                if (Compare(now, item)) count++;
            }
            if (count != 0) {
                // 한끼당 얼마만큼의 칼로리를 섭취해야되는지 계산 (남은 칼로리양 / 남은 알람 수)
                Double meal = (AdultManRecommendedCalories - dayCaloie) / count;
                Log.i("FoodRecommender", "daily.getCalorieSum() : " + dayCaloie + "\t leftAlarmCount" + alarmList.size() + "(" + meal + ")");
                Double weight = 0.1;
                RealmResults<FoodObject> foodResults = MainViewModel.Foods.where().between("KCAL", meal * (1 - weight), meal * (1 + weight)).limit(10).findAll();
                Log.i("FoodRecommender", "foodResults " + foodResults.size());

                recommends.addAll(MainViewModel.DailyDatas.getRealm().copyFromRealm(foodResults));
            }

        } else {

            Log.i("FoodRecommender", "daily.getCalorieSum() : ");
        }

        return recommends;
    }


    public boolean Compare(Calendar now, AlarmListItem alarm) {
        Log.i("FoodRecommender", "" + now.get(Calendar.HOUR_OF_DAY) + "\t " + alarm.getHour());
        if (alarm.getHour() > now.get(Calendar.HOUR_OF_DAY)) {
            return true;
        } else if (alarm.getHour() == now.get(Calendar.HOUR_OF_DAY)) {
            if (alarm.getMinute() >= now.get(Calendar.MINUTE)) {
                return true;
            }
        }
        return false;
    }


}
