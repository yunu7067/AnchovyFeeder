package com.example.anchovyfeeder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.annotations.RealmModule;

public class MainActivity extends AppCompatActivity {
    @RealmModule(classes = {FoodObject.class})
    public class BundledRealmModule {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);

        //ConvertCSVtoRealm();
        LoadRealmAndView();
        //CreateOrLoadAlarmListByRealm();
        AttachMainActivityItems();
    }

    public void ConvertCSVtoRealm() {
        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int count = 0;
                        try {
                            InputStreamReader is = new InputStreamReader(getResources().openRawResource(R.raw.list1));
                            BufferedReader reader = new BufferedReader(is);
                            CSVReader read = new CSVReader(reader);
                            String[] record = null;
                            while ((record = read.readNext()) != null) {
                                if (count++ == 0) continue;
                                //Log.d("RECORD:::::: ", record[0]);
                                FoodObject fo = realm.createObject(FoodObject.class);
                                fo.setNO(Long.valueOf(record[0]));
                                fo.setFOOD_NAME(record[1]);
                                fo.setFOOD_TYPE(record[2]);
                                fo.setAMOUNT_PER_SERVINGS(Double.valueOf(record[3]));
                                fo.setUNIT(record[4]);
                                fo.setKCAL(Double.valueOf(record[5]));
                                fo.setPROTEIN(Double.valueOf(record[6]));
                                fo.setFAT(Double.valueOf(record[7]));
                                fo.setCARBOHYDRATE(Double.valueOf(record[7]));
                                mRealm.insert(fo);
                                count++;
                            }
                        } catch (Exception e) {
                            Log.d("ERROR:: ", count + ":" + e.getMessage());
                        }
                    }
                }
        );


        TextView tv = (TextView) findViewById(R.id.textarea);
        tv.append("Realm 테스트\n");
        FoodObject fo = mRealm.where(FoodObject.class).equalTo("NO", 3).findFirst();
        if (fo != null) {
            //vo2.deleteFromRealm();
            tv.append("NO : " + fo.getNO() + "\tFOOD_NAME : " + fo.getFOOD_NAME());
        } else {
            tv.append("NULL");
        }

    }

    public void LoadRealmAndView() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .assetFile("default.realm")
                .readOnly()
                .modules(new BundledRealmModule())
                .build();

        Realm mRealm = Realm.getInstance(config);

        TextView tv = (TextView) findViewById(R.id.textarea);
        tv.append("Realm 테스트\n");
        FoodObject fo = mRealm.where(FoodObject.class)
                .contains("FOOD_NAME", "홍차")
                .findFirst();
        if (fo != null) {
            //vo2.deleteFromRealm();
            tv.append("NO : " + fo.getNO() + "\tFOOD_NAME : " + fo.getFOOD_NAME());
        } else {
            tv.append("NULL");
        }
    }

    public void CreateOrLoadAlarmListByRealm() {
        final Realm mRealm;
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("userdatas.realm")
                .modules(new BundledRealmModule())
                .build();

        mRealm = Realm.getInstance(config);

        mRealm.executeTransaction(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        AlarmObject ao = realm.createObject(AlarmObject.class);
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        //ao.setALARM_TIME();
                        ao.setALARM_TITLE("아침");
                        ao.setIS_USING(true);
                        mRealm.insert(ao);
                    }
                }
        );
    }

    public void AttachMainActivityItems() {
        final String[] listViewitems = {"호랑이", "토끼", "곰", "돌고래", "지렁이",
                "굴", "얼리버드", "타잔", "땃쥐", "뱁새"};
        final ArrayList<AlarmListItem> list = new ArrayList<AlarmListItem>();
        for (String item : listViewitems) {
            AlarmListItem listitem = new AlarmListItem();
            listitem.setName(item);
            listitem.setTime(12, 12, 00);
            listitem.setUse(true);

            list.add(listitem);
        }

        RecyclerView AlarmList = (RecyclerView) findViewById(R.id.AlarmList);

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        final AlarmListAdaper adapter = new AlarmListAdaper(list);
        AlarmList.setAdapter(adapter);
        // 구분선
        AlarmList.addItemDecoration(new DividerItemDecoration(
                getApplicationContext(), DividerItemDecoration.VERTICAL)
        );
        AlarmList.setLayoutManager(new LinearLayoutManager(this));

        ImageButton addAlarmButton = (ImageButton) findViewById(R.id.CardHeaderAdd);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmAddOrUpdateDialog aldial = new AlarmAddOrUpdateDialog(view.getContext(), adapter, list);
                aldial.show();
            }
        });
        /*
        Calendar mC = Calendar.getInstance();
        mC.set(Calendar.HOUR_OF_DAY, 21);
        mC.set(Calendar.MINUTE, 37);
        mC.set(Calendar.SECOND, 00);

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if (mC.before(Calendar.getInstance())) {
            mC.add(Calendar.DATE, 1);
        }
        Date currentDateTime = list.get(1).getTime().getTime();
        String date_text = new SimpleDateFormat("a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
        //Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


        //  Preference에 설정한 값 저장
        SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
        //editor.putLong("nextNotifyTime", (long) mC.getTimeInMillis());
        editor.putLong("nextNotifyTime", (long) list.get(1).getTime().getTimeInMillis());
        editor.apply();

        diaryNotification(list.get(1).getTime());
        */


        // WorkManager 테스트용
        // https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work?hl=ko#schedule_periodic_work
        // PeriodicWorkRequest를 이용하여 주기적으로 실행되는 WorkRequest 객체를 생성 (1일 간격)

        /*
        Data mData = new Data.Builder()
                .putString("name", "123")
                .build();
        */
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(AlarmWorker.class, 1, TimeUnit.DAYS)
                        // 작업을 식별하는데 사용하는 태그 지정
                        .addTag("테그")
                        // 입력 데이터 할당. 값은 Data 객체에 Key-Value 쌍으로 저장된다
                        //.setInputData(mData)
                        // 특정 시간에 시작하는 함수가 없으므로 지연을 사용하여 시작 시간을 설정해야 한다.
                        .setInitialDelay(10, TimeUnit.SECONDS)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                30,
                                TimeUnit.MINUTES
                        )
                        .build();

        //Toast.makeText(getApplicationContext(), saveRequest.getTags().toArray()[1].toString(), Toast.LENGTH_SHORT).show();

        // WorkManager 큐에 추가
        WorkManager
                .getInstance(getApplicationContext())
                .enqueue(saveRequest);
    }
    /*
    void diaryNotification(Calendar calendar) {
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        //ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);

        }
    }
    */
}