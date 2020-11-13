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
import android.graphics.Color;
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
//MPAndroidChart import
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.LineChart;

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
                "굴", "땃쥐", "뱁새"};
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
                        //.setInitialDelay(5, TimeUnit.SECONDS)
                        /*
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                30,
                                TimeUnit.MINUTES
                        )*/
                        .build();

        PeriodicWorkRequest saveRequest2 =
                new PeriodicWorkRequest.Builder(AlarmWorker.class, 1, TimeUnit.DAYS)
                        // 작업을 식별하는데 사용하는 태그 지정
                        .addTag("테그")
                        // 입력 데이터 할당. 값은 Data 객체에 Key-Value 쌍으로 저장된다
                        //.setInputData(mData)
                        // 특정 시간에 시작하는 함수가 없으므로 지연을 사용하여 시작 시간을 설정해야 한다.
                        .setInitialDelay(15, TimeUnit.SECONDS)
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
        WorkManager
                .getInstance(getApplicationContext())
                .enqueue(saveRequest2);

        drawGraph();
    }

       public void drawGraph() {
        LineChart lineChart = findViewById(R.id.chart);

        ArrayList<Entry> entry1 = new ArrayList<>();
        ArrayList<Entry> entry2 = new ArrayList<>();

        //그래프에 들어갈 좌표값 입력
        entry1.add(new Entry(0, 1));
        entry1.add(new Entry(1, 12));
        entry1.add(new Entry(2, 3));

        entry2.add(new Entry(0, 1));
        entry2.add(new Entry(1, 3));
        entry2.add(new Entry(2, 5));

        LineData chartData = new LineData();
        LineDataSet set1 = new LineDataSet(entry1, "라벨명1");
        chartData.addDataSet(set1);

        LineDataSet set2 = new LineDataSet(entry2, "라벨명2");
        chartData.addDataSet(set2);

        lineChart.setData(chartData);
        lineChart.invalidate();
    }

}