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
import com.example.anchovyfeeder.realmdb.FoodObject;
import com.example.anchovyfeeder.realmdb.WeightObject;
import com.example.anchovyfeeder.realmdb.utils.DateCalculator;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
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
 *   칼로리는 1:n, 몸무게는 1:1
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

        //그래프에 들어갈 좌표값 입력
        calEntry.add(new Entry(1, 2000));
        calEntry.add(new Entry(2, 3200));
        calEntry.add(new Entry(3, 2100));
        calEntry.add(new Entry(6, 2500));

        weightEntry.add(new Entry(1, 57f));
        weightEntry.add(new Entry(2, 58f));
        weightEntry.add(new Entry(5, 60f));
        weightEntry.add(new Entry(7, 61.2f));

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
        // Chart Data Observer
        viewModel.calEntry.observe(this, entries -> {
            calEntry = entries;
            setChartData();
        });
        viewModel.weightEntry.observe(this, entries -> {
            weightEntry = entries;
            setChartData();
        });


        // Realm Database Initialization
        if (!initRealm())
            Toast.makeText(context, "데이터베이스 로드 실패", Toast.LENGTH_SHORT).show();

        // Realm Object Observer
        viewModel.weightsThisMonth.addChangeListener((results) -> {
            Log.i("시발", "변경감지!!!!!!!! + " + results.size());
            //changeSet.getInsertions(); // => [0] is added.
            ArrayList<Entry> list = new ArrayList<>();
            RealmResults<WeightObject> temp = results.sort("DATE");
            for (WeightObject item : temp) {
                list.add(item.getEntry());
            }
            weightEntry = list;
            setChartData();
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
            loadWeightsInRealm();
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

    public void loadFoodRealFile() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .assetFile("default.realm")
                .readOnly()
                .modules(new BundledRealmModule())
                .build();

        Realm mRealm = Realm.getInstance(config);

        TextView tv = (TextView) findViewById(R.id.textarea);
        tv.append("Realm 테스트\n");
        MainViewModel.foodsRealm = mRealm.where(FoodObject.class).findAll();
        FoodObject fo = MainViewModel.foodsRealm.where().contains("FOOD_NAME", "홍차").findFirst();
        if (fo != null) {
            //vo2.deleteFromRealm();
            tv.append("NO : " + fo.getNO() + "\tFOOD_NAME : " + fo.getFOOD_NAME());
        } else {
            tv.append("NULL");
        }
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
    private void loadWeightsInRealm() {
        myRealm.executeTransaction(_realm -> {
            MainViewModel.weightsThisMonth = _realm.where(WeightObject.class)
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
            TextView chartDescTextView = findViewById(R.id.chartDesc);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    String[] descs = getResources().getStringArray(R.array.chart_type_desc_array);
                    String selectedType = adapterView.getItemAtPosition(pos).toString();
                    ArrayList<Entry> list = new ArrayList<Entry>();
                    MyValueFormatter fomatter = new MyValueFormatter();
                    XAxis xAxis = ((LineChart) findViewById(R.id.chart)).getXAxis();
                    fomatter.setDecimalFormat("##0");

                    android.util.Log.i("chartTypeSpinner", "Value (" + selectedType + ") ");

                    if ("일별".equals(selectedType)) {
                        Date[] dates = dateCalculator.getFirstAndLastDayOfMonth();
                        fomatter.setUnit("일");
                        // WeightList 설정
                        RealmResults<WeightObject> results =
                                MainViewModel.weightsThisMonth.where().between("DATE", dates[0], dates[1]).findAll().sort("DATE");
                        for (WeightObject item : results) {
                            list.add(item.getEntry());
                        }
                        // CalorieList 설정

                    } else if ("주별".equals(selectedType)) {
                        Date[] dates = dateCalculator.getLastAndNext3WeekOfNow();
                        fomatter.setUnit("주 전");
                        // WeightList 설정
                        RealmResults<WeightObject> results =
                                MainViewModel.weightsThisMonth.where().between("DATE", dates[0], dates[1]).findAll().sort("DATE");
                        int week = -6;
                        for (int i = 0; i < 7; i++) {
                            float avgWeight = (float) MainViewModel.weightsThisMonth.where().between("DATE", dates[i * 2], dates[i * 2 + 1]).average("WEIGHT");
                            if (avgWeight > 0.0) {
                                list.add(new Entry(week, avgWeight));
                            }
                            week++;
                        }
                        // CalorieList 설정

                    } else if ("분기별".equals(selectedType)) {


                    }

                    xAxis.setValueFormatter(fomatter);
                    chartDescTextView.setText(descs[pos]);
                    MainViewModel.weightEntry.setValue(list);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            createGraph();
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

        // 칼로리 추가
        findViewById(R.id.addCaloris).setOnClickListener(view -> {
            CalorieAdderDialog dial = new CalorieAdderDialog(view.getContext());
            dial.show();
            boolean isExist = false;
            for (Entry entry : calEntry) {
                if (entry.getX() == 10) {
                    entry.setY(entry.getY() + 10f);
                    isExist = true;
                }
            }
            if (!isExist)
                calEntry.add(new Entry(10, 20f));
            viewModel.calEntry.setValue(calEntry);
        });

        // 몸무게 추가
        findViewById(R.id.addWeights).setOnClickListener(view -> {
            //Toast.makeText(getApplicationContext(), "구현중", Toast.LENGTH_SHORT).show();
            WeightAdderDialog dial = new WeightAdderDialog(view.getContext());
            dial.show();
        });
    }

    private void createGraph() {
        LineChart myChart = findViewById(R.id.chart);
        myChart.setScaleEnabled(false);
        myChart.getDescription().setEnabled(false);

        XAxis xAxis = myChart.getXAxis();
        MyValueFormatter vf = new MyValueFormatter();
        vf.setDecimalFormat("##0");
        vf.setUnit("일");
        xAxis.setValueFormatter(vf);
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
        rightAxis.setAxisMaximum(125f);
        rightAxis.setAxisMinimum(30f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setGranularityEnabled(false);
        myChart.invalidate();
    }

    public void setChartData() {
        final LineChart chart = findViewById(R.id.chart);
        chart.animateY(1000);
        chart.invalidate();
        chart.clear();

        LineDataSet calSet, weightSet;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            calSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            calSet.setValues(calEntry);

            weightSet = (LineDataSet) chart.getData().getDataSetByIndex(1);
            weightSet.setValues(weightEntry);

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();

        } else {
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
            MyValueFormatter weightFormatter = new MyValueFormatter();
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