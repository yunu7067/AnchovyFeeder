package com.example.anchovyfeeder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AlarmNotifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.util.Log.i("알람 액티비티", "onCreate()");


        setContentView(R.layout.alarm_dialog);

        TextView title = (TextView) findViewById(R.id.alarm_dialog_title);
        String APP_NAME = getString(R.string.app_name);
        String ALARM_NAME = getIntent().getStringExtra("NAME");
        title.setText(APP_NAME + " - " + ALARM_NAME);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.i("알람 액티비티", "onDestroy()");
    }
}