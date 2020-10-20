package com.example.anchovyfeeder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
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
    }

    public void ConvertCSVtoRealm() {
        Realm mRealm = Realm.getDefaultInstance();
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
        FoodObject fo = mRealm.where(FoodObject.class).contains("FOOD_NAME", "홍차").findFirst();
        if (fo != null) {
            //vo2.deleteFromRealm();
            tv.append("NO : " + fo.getNO() + "\tFOOD_NAME : " + fo.getFOOD_NAME());
        } else {
            tv.append("NULL");
        }
    }

    public void CreateOrLoadAlarmListByRealm() {
        Realm mRealm;
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
}