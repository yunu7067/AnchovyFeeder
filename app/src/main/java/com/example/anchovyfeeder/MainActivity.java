package com.example.anchovyfeeder;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.anchovyfeeder.databinding.ActivityMainBinding;
import com.example.anchovyfeeder.realmdb.DailyDataObject;
import com.example.anchovyfeeder.realmdb.FoodObject;
import com.example.anchovyfeeder.realmdb.utils.DateCalculator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;

/*
 * TODO [ 예정 ]
 *   날짜가 변경되면 자동으로 WorkManager 알람 다시 Request하기
 *   알람 오면 소리나 진동 같이 울리게
 *   알람 오고 미루기 누르면 알림에 보여주기
 *   누르면 바로 알람창 다시 띄우기
 *
 * TODO [ 진행중 ]
 * */

public class MainActivity extends AppCompatActivity {
    MainActivity context = this;
    ActivityMainBinding binding;
    ArrayList<AlarmListItem> alarmList = new ArrayList<AlarmListItem>();
    ArrayList<Entry> calEntry = new ArrayList<>();
    ArrayList<Entry> weightEntry = new ArrayList<>();
    DateCalculator dateCalculator = new DateCalculator();

    @RealmModule(classes = {FoodObject.class})
    public class BundledRealmModule {

    }

    Realm myRealm;
    RealmConfiguration myRealmConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewModel Data binding
        binding = DataBindingUtil.setContentView(context, R.layout.activity_main);
        final MainViewModel viewModel = new ViewModelProvider(context).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(context);
        // Alarm List Observer
        viewModel.alarmList.observe(this, alarmList -> {
            Toast.makeText(context, "알람 재설정", Toast.LENGTH_SHORT).show();
            this.alarmList = alarmList;
            resetAlarm();
            saveAlarmListInRealm();
        });
        // Realm Database Initialization
        if (!initRealm())
            Toast.makeText(context, "데이터베이스 로드 실패", Toast.LENGTH_SHORT).show();
        // Realm Database Observer
        viewModel.DailyDatas.addChangeListener((results) -> {
            Log.i("ViewModel.DailyDatas", "변경감지!!!!!!!! + " + results.size());
            Spinner sp = findViewById(R.id.chartTypeSpinner);
            Log.i("ViewModel.DailyDatas", "ITEM(" + sp.getSelectedItem().toString() + ")  POS(" + sp.getSelectedItemPosition());
            refreshChartEntries(sp.getSelectedItem().toString(), sp.getSelectedItemPosition());
        });

        //ConvertCSVtoRealm();
        loadFoodRealFile();
        initComponents();
    }


    private boolean initRealm() {
        try {
            Realm.init(context);
            myRealmConfig = new RealmConfiguration.Builder().name("userdata.realm").build();
            Realm.setDefaultConfiguration(myRealmConfig);
            myRealm = Realm.getDefaultInstance();
            loadAlarmListInRealm();
            //loadWeightsInRealm();
            loadUserDatasInRealm();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean resetRealm() {
        try {
            if (!myRealm.isClosed())
                myRealm.close();
            Realm.deleteRealm(myRealmConfig);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void ConvertCSVtoRealm() {
        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(
                realm -> {
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

    public void loadFoodRealFile() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .assetFile("default.realm")
                .readOnly()
                .modules(new BundledRealmModule())
                .build();

        Realm mRealm = Realm.getInstance(config);

        TextView tv = (TextView) findViewById(R.id.textarea);
        tv.append("Realm 테스트\n");
        RealmResults<FoodObject> results = mRealm.where(FoodObject.class).findAll();
        ArrayList<FoodObject> foods = new ArrayList<>();
        if (!results.isEmpty())
            foods.addAll(mRealm.copyFromRealm(results));
        MainViewModel.foodList = foods;
        android.util.Log.i("loadFoodRealFile()", "음식 목록" + foods.size() + "개 로드");
/*
        FoodObject fo = MainViewModel.foodsRealm.where().like("FOOD_NAME", "홍차").findFirst();
        if (fo != null) {
            //vo2.deleteFromRealm();
            tv.append("NO : " + fo.getNO() + "\tFOOD_NAME : " + fo.getFOOD_NAME());
        } else {
            tv.append("NULL");
        }*/
    }

    /**
     * Realm 데이터베이스에서 AlarmListItem를 전부 불러온 후, ArrayList 형식으로 변환하여 ViewModel의 AlarmList 객체에 저장한다.
     */
    private void loadAlarmListInRealm() {
        android.util.Log.i("loadAlarmListReam()", "알람 목록 불러오기 시작");
        alarmList.clear();
        myRealm.executeTransaction(_realm -> {
            RealmResults<AlarmListItem> results = _realm.where(AlarmListItem.class).findAll();
            if (!results.isEmpty())
                alarmList.addAll(_realm.copyFromRealm(results));
            android.util.Log.i("loadAlarmListReam()", "총 " + alarmList.size() + "개 불러옴");
        });
        android.util.Log.i("loadAlarmListReam()", "알람 목록 불러오기 종료");
    }

    /**
     * AlarmList(ArrayList 타입)을 RealmObject 으로 변환하여 Realm 데이터베이스에 저장한다.
     */
    private void saveAlarmListInRealm() {
        android.util.Log.i("saveAlarmListReam()", "알람 목록 저장 시작");
        myRealm.executeTransaction(_realm -> {
            _realm.delete(AlarmListItem.class);
            _realm.copyToRealm(alarmList);
        });
        android.util.Log.i("saveAlarmListReam()", "알람 목록 저장 종료");
    }

    /**
     * Realm 데이터베이스에 있는 WeightObject를 전부 불러온 후 ViewModel에 바인드한다.
     */
   /* private void loadWeightsInRealm() {
        myRealm.executeTransaction(_realm -> {
            MainViewModel.weightsThisMonth = _realm.where(WeightObject.class)
                    .findAll();
        });
    }*/
    private void loadUserDatasInRealm() {
        myRealm.executeTransaction(_realm -> {
            MainViewModel.DailyDatas = _realm.where(DailyDataObject.class)
                    .findAll();
        });
    }


    public void initComponents() {
        // AlarmList CardView
        {
            MainViewModel.setAlarmList(alarmList);

            RecyclerView AlarmList = findViewById(R.id.AlarmList);

            // 리사이클러뷰에 Adapter 객체 지정.
            final AlarmListAdaper adapter = new AlarmListAdaper(alarmList);
            AlarmList.setAdapter(adapter);
            // 아이템간 구분선
            AlarmList.addItemDecoration(new DividerItemDecoration(
                    getApplicationContext(), DividerItemDecoration.VERTICAL)
            );
            AlarmList.setLayoutManager(new LinearLayoutManager(this));

            ImageButton addAlarmButton = findViewById(R.id.CardHeaderAdd);
            addAlarmButton.setOnClickListener(view -> {
                //Toast.makeText(view.getContext(), "알람 목록(현재" + alarmList.size() + "개)", Toast.LENGTH_SHORT).show();
                AlarmAddOrUpdateDialog aldial = new AlarmAddOrUpdateDialog(view.getContext(), adapter, alarmList);
                aldial.show();
            });
        }

        // Chart CardView
        {
            Spinner spinner = findViewById(R.id.chartTypeSpinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    String selectedType = adapterView.getItemAtPosition(pos).toString();
                    refreshChartEntries(selectedType, pos);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //그래프에 들어갈 임시값 입력
            calEntry.add(new Entry(0, 2700));
            weightEntry.add(new Entry(0, 50f));
            initChart();
            setChartData();
        }
        setEvent();
    }

    private void resetAlarm() {
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
            //android.util.Log.i("WorkManager Request", nowHour + "<->" + item.getHour());
            //android.util.Log.i("WorkManager Request", nowMinute + "<->" + item.getMinute());
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

    private void setEvent() {
        final MainViewModel viewModel = new ViewModelProvider(context).get(MainViewModel.class);

        // 칼로리 추가 버튼
        findViewById(R.id.addCaloris).setOnClickListener(view -> {
            CalorieAdderDialog dial = new CalorieAdderDialog(view.getContext());
            dial.show();
        });
        // 몸무게 추가 버튼
        findViewById(R.id.addWeights).setOnClickListener(view -> {
            //Toast.makeText(getApplicationContext(), "구현중", Toast.LENGTH_SHORT).show();
            WeightAdderDialog dial = new WeightAdderDialog(view.getContext());
            dial.show();
        });
    }

    /**
     * MPChart를 초기화하는 메서드
     */
    private void initChart() {
        LineChart chart = findViewById(R.id.chart);
        //chart.setViewPortOffsets(0, 0, 0, 0);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        //chart.setDrawGridBackground(false);
        //chart.setMaxHighlightDistance(300);
        //chart.setMaxVisibleValueCount(7);
        //chart.setDragOffsetX(30f);
        //chart.setVisibleXRangeMaximum(32f);
        //chart.moveViewToX(20f);

        // 가로축
        XAxis xAxis = chart.getXAxis();
        MyValueFormatter vf = new MyValueFormatter();
        vf.setDecimalFormat("##0");
        vf.setUnit("일");
        xAxis.setValueFormatter(vf);
        xAxis.setTextSize(11);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(32f);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 세로축 (왼쪽)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawLabels(false);

        // 세로축 (오른쪽)
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setAxisMaximum(125f);
        rightAxis.setAxisMinimum(30f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setGranularityEnabled(false);

        //chart.getLegend().setEnabled(false);
        chart.animateXY(1000, 1000);

        // 차트 다시 그리기
        chart.invalidate();
    }


    private void refreshChartEntries(String selectedType, int pos) {
        String[] descs = getResources().getStringArray(R.array.chart_type_desc_array);
        ArrayList<Entry> weightList = new ArrayList<Entry>();
        ArrayList<Entry> calorieList = new ArrayList<Entry>();
        MyValueFormatter fomatter = new MyValueFormatter();

        LineChart chart = findViewById(R.id.chart);
        XAxis xAxis = chart.getXAxis();
        YAxis leftYAxis = chart.getAxisLeft();
        fomatter.setDecimalFormat("##0");

        android.util.Log.i("chartTypeSpinner", "Value (" + selectedType + ") ");

        if ("일별".equals(selectedType)) {
            // 왼쪽 세로축 제한선
            leftYAxis.removeAllLimitLines();
            LimitLine ll = new LimitLine(2000f, "성인 여성 일일 권장 칼로리");
            ll.setLineColor(Color.RED);
            ll.setLineWidth(1.5f);
            ll.enableDashedLine(3.5f, 2f, 1);
            ll.setTextColor(Color.BLACK);
            ll.setTextSize(12f);
            leftYAxis.addLimitLine(ll);
            LimitLine ll_man = new LimitLine(2700f, "성인 남성 일일 권장 칼로리");
            ll_man.setLineColor(Color.RED);
            ll_man.setLineWidth(1.5f);
            ll_man.enableDashedLine(3.5f, 2f, 1);
            ll_man.setTextColor(Color.BLACK);
            ll_man.setTextSize(12f);
            leftYAxis.addLimitLine(ll_man);

            Date[] dates = dateCalculator.getFirstAndLastDayOfMonth();
            fomatter.setUnit("일");
            RealmResults<DailyDataObject> dailys = MainViewModel.DailyDatas
                    .where()
                    .between("DATE", dates[0], dates[1])
                    .findAll()
                    .sort("DATE");
            for (DailyDataObject item : dailys) {
                if (item.getWEIGHT() > 0.0)
                    weightList.add(item.getWeightEntry());
                if (item.getCalorieSum() > 0.0)
                    calorieList.add(item.getCalorieEntry());
            }
            android.util.Log.i("chartTypeSpinner", "Array Size: " + weightList.size() + " and " + calorieList.size());

        } else if ("주별".equals(selectedType)) {
            // 왼쪽 세로축 제한선
            leftYAxis.removeAllLimitLines();
            android.util.Log.i("개시발진짜좆같은새끼", "count:  " + xAxis.getLabelCount());


            Date[] dates = dateCalculator.getLast7WeeksToDates();
            fomatter.setUnit("주 전");
            int week = -6;
            for (int i = 0; i < 6; i++) {
                RealmResults<DailyDataObject> results = MainViewModel.DailyDatas
                        .where()
                        .between("DATE", dates[i * 2], dates[i * 2 + 1])
                        .findAll()
                        .sort("DATE");
                float weightAvg = (float) results.average("WEIGHT");
                float caloriesSum = 0.0f;
                for (DailyDataObject item : results) {
                    caloriesSum += item.getCalorieSum();
                }
                if (weightAvg > 0.0)
                    weightList.add(new Entry(week, weightAvg));
                if (caloriesSum > 0.0)
                    calorieList.add(new Entry(week, caloriesSum));
                week++;
            }
            android.util.Log.i("chartTypeSpinner", "Array " + weightList);

        } else if ("분기별".equals(selectedType)) {
            // 미구현

        }
        // 차트 가로축 단위 설정
        xAxis.setValueFormatter(fomatter);
        // 차트 가로축 최대 범위 설정
        float last = 7f;
        last = (float) dateCalculator.getDateNow();
        if (!weightList.isEmpty()) {
            Entry weightLastEntry = weightList.get(weightList.size() - 1);
            float lastEntryX = weightLastEntry.getX();
            last = (lastEntryX > last) ? lastEntryX : last;
        }
        if (!calorieList.isEmpty()) {
            Entry caloriesLastEntry = calorieList.get(calorieList.size() - 1);
            float lastEntryX = caloriesLastEntry.getX();
            last = (lastEntryX > last) ? lastEntryX : last;
        }
        xAxis.setAxisMaximum(last);

        TextView chartDescTextView = findViewById(R.id.chartDesc);
        chartDescTextView.setText(descs[pos]);


        weightEntry = weightList;
        calEntry = calorieList;
        setChartData();
    }

    public void setChartData() {
        LineChart chart = findViewById(R.id.chart);
        LineDataSet calSet, weightSet;

        /*if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            calSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            calSet.setValues(calEntry);

            weightSet = (LineDataSet) chart.getData().getDataSetByIndex(1);
            weightSet.setValues(weightEntry);

            calSet.notifyDataSetChanged();
            weightSet.notifyDataSetChanged();
            chart.notifyDataSetChanged();

        } else {*/
            calSet = new LineDataSet(calEntry, "칼로리");
            calSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            calSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            MyValueFormatter calorieFormatter = new MyValueFormatter();
            calorieFormatter.setUnit("Kcal");
            calSet.setValueFormatter(calorieFormatter);
            calSet.setCubicIntensity(0.2f);
            calSet.setDrawFilled(true);
            calSet.setDrawCircles(false);
            calSet.setLineWidth(1.8f);
            calSet.setCircleRadius(4f);
            calSet.setValueTextSize(10f);
            calSet.setCircleColor(R.color.colorLine1Pressed);
            calSet.setHighLightColor(Color.rgb(244, 117, 117));
            calSet.setColor(getColor(R.color.colorLine1));
            calSet.setFillColor(getColor(R.color.colorLine1));
            calSet.setFillAlpha(200);
            calSet.setDrawHorizontalHighlightIndicator(false);
            calSet.setFillFormatter((dataSet, dataProvider) -> chart.getAxisLeft().getAxisMinimum());

            weightSet = new LineDataSet(weightEntry, "몸무게");
            weightSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            weightSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            MyValueFormatter weightFormatter = new MyValueFormatter();
            weightFormatter.setUnit("Kg");
            weightSet.setValueFormatter(weightFormatter);
            weightSet.setCubicIntensity(0.2f);
            weightSet.setDrawFilled(false);
            weightSet.setDrawCircles(true);
            weightSet.setLineWidth(1.8f);
            weightSet.setValueTextSize(10f);
            weightSet.setCircleRadius(4f);
            weightSet.setCircleColor(R.color.colorLine2Pressed);
            weightSet.setHighLightColor(Color.rgb(244, 117, 117));
            weightSet.setColor(getColor(R.color.colorLine2));
            weightSet.setFillColor(getColor(R.color.colorLine2));
            weightSet.setFillAlpha(0);
            weightSet.setDrawHorizontalHighlightIndicator(false);
            weightSet.setFillFormatter((dataSet, dataProvider) -> chart.getAxisLeft().getAxisMinimum());

            // create a data object with the data sets
            LineData chartData = new LineData();
            chartData.addDataSet(calSet);
            chartData.addDataSet(weightSet);
            chartData.setDrawValues(true);

            // set data
            chart.setData(chartData);
        //}
        chart.fitScreen();
        chart.setVisibleXRangeMaximum(7f);
        chart.moveViewToX(chart.getXRange());
        chart.invalidate();
    }

    // 백 버튼
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if ((0 <= intervalTime) && (FINISH_INTERVAL_TIME >= intervalTime)) {
            finish();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

}