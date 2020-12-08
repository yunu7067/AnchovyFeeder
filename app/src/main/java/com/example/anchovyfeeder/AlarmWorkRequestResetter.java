package com.example.anchovyfeeder;

import android.content.Context;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlarmWorkRequestResetter {
    Context context;
    ArrayList<AlarmListItem> alarmList = new ArrayList<AlarmListItem>();

    public AlarmWorkRequestResetter(Context context, ArrayList<AlarmListItem> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    public AlarmWorkRequestResetter() {
    }

    public AlarmWorkRequestResetter setContext(Context context) {
        this.context = context;
        return this;
    }

    public AlarmWorkRequestResetter setAlarmList(ArrayList<AlarmListItem> alarmList) {
        this.alarmList = alarmList;
        return this;
    }

    public void resetAlarm() {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWork();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date(System.currentTimeMillis()));
        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);
        int nowSecond = nowCalendar.get(Calendar.SECOND);
        int delaySecond = 0;

        for (AlarmListItem item : alarmList) {
            // 현재 시간 이전에 있는 아이템은 큐에 추가 안함
            android.util.Log.i("WorkManager Request", nowHour + "<->" + item.getHour());
            android.util.Log.i("WorkManager Request", nowMinute + "<->" + item.getMinute());
            if (item.getHour() < nowHour) {
                // 알람 시간이 현재시간보다 작으면 무조건 건너뀜
                //android.util.Log.i("WorkManager Request", "Skip1");
                continue;
            } else if ((item.getHour() == nowHour) && (item.getMinute() <= nowMinute)) {
                // 알람 시각이 현재 시각과 같고, 알람 분이 현재 분보다 작거나 같을 때 건너뜀
                //android.util.Log.i("WorkManager Request", "Skip2");
                continue;
            }
            delaySecond = (item.getHour() - nowHour) * 60 * 60 + (item.getMinute() - nowMinute) * 60 - nowSecond;
            // Request 생성
            WorkRequest dealyWorkRequest = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                    .setInitialDelay(delaySecond, TimeUnit.SECONDS)
                    .addTag(item.getName())
                    .build();
            // WorkManager 큐에 추가
            workManager.enqueue(dealyWorkRequest);
            android.util.Log.i("WorkManager Request", "Delay(" + delaySecond + "초), Tag(" + item.getName() + ") ");
        }
    }

}
