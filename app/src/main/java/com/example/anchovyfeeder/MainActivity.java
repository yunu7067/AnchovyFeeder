package com.example.anchovyfeeder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.annotations.RealmModule;

public class MainActivity extends AppCompatActivity {
    @RealmModule(classes={FoodObject.class})
    public class BundledRealmModule {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .assetFile("foodinfos.realm")
                .readOnly()
                .modules(new BundledRealmModule())
                .build();

        Realm mRealm = Realm.getInstance(config);


        /*
        Realm.init(this);
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(
                new Realm.Transaction() {
                    @Override
                    public  void execute(Realm realm) {
                        MemoVO vo = realm.createObject(MemoVO.class);
                        vo.title = "test";
                        vo.content = "123";

                    }
                }
        );
        */
        //Realm mRealm2 = Realm.getDefaultInstance();
        TextView tv = (TextView) findViewById(R.id.textarea);
        tv.append("Realm 테스트\n");

        FoodObject vo2 = mRealm.where(FoodObject.class).equalTo("FOOD_NAME", "훈제오리").findFirst();
        //vo2.deleteFromRealm();
        tv.append("title : " + vo2.getFOOD_NAME() + "\ncontent : " + vo2.getKCAL());



    }
}