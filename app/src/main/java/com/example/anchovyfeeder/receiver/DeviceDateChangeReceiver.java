package com.example.anchovyfeeder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.anchovyfeeder.AlarmListItem;
import com.example.anchovyfeeder.AlarmWorkRequestResetter;

import java.util.ArrayList;

public class DeviceDateChangeReceiver extends BroadcastReceiver {
    ArrayList<AlarmListItem> alarmList = new ArrayList<AlarmListItem>();
    AlarmWorkRequestResetter alarm = new AlarmWorkRequestResetter();

    public void setAlarmList(ArrayList<AlarmListItem> alarmList) {
        this.alarmList = alarmList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (Intent.ACTION_DATE_CHANGED.equals(action)) {
            alarm.setAlarmList(alarmList).setContext(context).resetAlarm();
            Log.i("ACTION_DATE_CHANGED", "알람 재설정");
        }
    }
}