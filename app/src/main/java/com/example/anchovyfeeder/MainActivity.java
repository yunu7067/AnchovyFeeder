package com.example.anchovyfeeder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(
                new Realm.Transaction() {
                    @Override
                    public  void execute(Realm realm) {
                        MemoVO vo = realm.createObject(MemoVO.class);
                        vo.title = "test";
                        vo.content = "content";

                    }
                }
        );
        //Realm mRealm2 = Realm.getDefaultInstance();
        TextView tv = (TextView) findViewById(R.id.textarea);
        tv.append("Realm 테스트\n");
        MemoVO vo2 = mRealm.where(MemoVO.class).equalTo("title", "test").findFirst();
        //vo2.deleteFromRealm();
        tv.append("title : " + vo2.title + "\ncontent : " + vo2.content);



    }
}