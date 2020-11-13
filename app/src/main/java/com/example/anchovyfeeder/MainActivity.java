package com.example.anchovyfeeder;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.annotations.RealmModule;

//MPAndroidChart import

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

        createGraph();
        setChartData();
    }

    private void setEvent() {
        // TODO : 칼로리 추가, 몸무게 추가 리스너 추가
    }

    private void createGraph() {
        LineChart myChart = findViewById(R.id.chart);
        myChart.setScaleEnabled(false);
        myChart.getDescription().setEnabled(false);

        XAxis xAxis = myChart.getXAxis();
        xAxis.setTextSize(11);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = myChart.getAxisLeft();
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = myChart.getAxisRight();
        rightAxis.setAxisMaximum(120f);
        rightAxis.setAxisMinimum(30f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setGranularityEnabled(false);
        myChart.invalidate();
    }

    public void setChartData() {
        final LineChart chart = findViewById(R.id.chart);
        chart.animateY(2000);

        ArrayList<Entry> calEntry = new ArrayList<>();
        ArrayList<Entry> weightEntry = new ArrayList<>();

        //그래프에 들어갈 좌표값 입력
        calEntry.add(new Entry(1, 2000));
        calEntry.add(new Entry(2, 3500));
        calEntry.add(new Entry(3, 2100));

        weightEntry.add(new Entry(1, 57f));
        weightEntry.add(new Entry(2, 58f));
        weightEntry.add(new Entry(3, 110f));
        weightEntry.add(new Entry(5, 61.2f));
        weightEntry.add(new Entry(31, 71.2f));

        LineDataSet calSet, weightSet;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            calSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            calSet.setValues(calEntry);

            weightSet = (LineDataSet) chart.getData().getDataSetByIndex(1);
            weightSet.setValues(weightEntry);

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            calSet = new LineDataSet(calEntry, "칼로리");
            calSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            calSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            myValueFormatter calorieFormatter = new myValueFormatter();
            calorieFormatter.setUnit("Kcal");
            calSet.setValueFormatter(calorieFormatter);
            calSet.setCubicIntensity(0.2f);
            calSet.setDrawFilled(true);
            calSet.setDrawCircles(false);
            calSet.setLineWidth(1.8f);
            calSet.setCircleRadius(4f);
            calSet.setValueTextSize(10f);
            calSet.setCircleColor(Color.WHITE);
            calSet.setHighLightColor(Color.rgb(244, 117, 117));
            calSet.setColor(getColor(R.color.colorLine1));
            calSet.setFillColor(getColor(R.color.colorLine1));
            calSet.setFillAlpha(200);
            calSet.setDrawHorizontalHighlightIndicator(false);
            calSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            weightSet = new LineDataSet(weightEntry, "몸무게");
            weightSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            weightSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            myValueFormatter weightFormatter = new myValueFormatter();
            weightFormatter.setUnit("Kg");
            weightSet.setValueFormatter(weightFormatter);
            weightSet.setCubicIntensity(0.2f);
            weightSet.setDrawFilled(false);
            weightSet.setDrawCircles(true);
            weightSet.setLineWidth(1.8f);
            weightSet.setValueTextSize(10f);
            weightSet.setCircleRadius(4f);
            weightSet.setCircleColor(Color.WHITE);
            weightSet.setHighLightColor(Color.rgb(244, 117, 117));
            weightSet.setColor(getColor(R.color.colorLine2));
            weightSet.setFillColor(getColor(R.color.colorLine2));
            weightSet.setFillAlpha(0);
            weightSet.setDrawHorizontalHighlightIndicator(false);
            weightSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
        }


        // create a data object with the data sets
        LineData chartData = new LineData();
        chartData.addDataSet(calSet);
        chartData.addDataSet(weightSet);

        // set data
        chart.setData(chartData);
        chart.setDragOffsetX(30f);

        chart.setVisibleXRangeMaximum(7);
        chart.moveViewToX(chart.getXRange());
    }
}