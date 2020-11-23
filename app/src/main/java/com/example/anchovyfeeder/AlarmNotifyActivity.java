package com.example.anchovyfeeder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class AlarmNotifyActivity extends AppCompatActivity {
    static String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.i("알람 액티비티", "onCreate()");

        // 권한 설정 확인 및 요청
        Boolean isCameraPermissionGranted = checkSelfPermission(PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
        Boolean isWriteStoragePermissionGranted = checkSelfPermission(PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
        if (isCameraPermissionGranted && isWriteStoragePermissionGranted) {
            android.util.Log.i("권한 설정", "완료");
        } else {
            android.util.Log.i("권한 설정", "요청");
            ActivityCompat.requestPermissions(AlarmNotifyActivity.this, PERMISSIONS, 1);
        }

        setContentView(R.layout.alarm_dialog);


        TextView title = findViewById(R.id.dialog_header_layout);
        String APP_NAME = getString(R.string.app_name);
        final String ALARM_NAME = getIntent().getStringExtra("NAME");
        title.setText(APP_NAME + " - " + ALARM_NAME);

        ImageButton cameraButton = findViewById(R.id.alarm_camera_button);
        ImageButton delayButton = findViewById(R.id.alarm_delay_button);

        cameraButton.setOnClickListener(view -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(cameraIntent, 1);

        });

        delayButton.setOnClickListener(view -> {
            // Work request 생성
            WorkRequest dealyWorkRequest =
                    new OneTimeWorkRequest.Builder(AlarmWorker.class)
                            .setInitialDelay(30, TimeUnit.MINUTES)
                            .addTag(ALARM_NAME)
                            .build();
            // WorkManager 큐에 추가
            WorkManager
                    .getInstance(getApplicationContext())
                    .enqueue(dealyWorkRequest);


            // 액티비티 종료
            finish();
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.i("알람 액티비티", "onDestroy()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 카메라
        Toast.makeText(getApplicationContext(), "아직 준비 안됨", Toast.LENGTH_SHORT).show();

        if (resultCode == RESULT_OK && data.hasExtra("data")) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
            }
        }
    }
}