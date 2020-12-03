package com.example.anchovyfeeder;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.anchovyfeeder.realmdb.PhotoObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class AlarmNotifyActivity extends AppCompatActivity {
    Uri alarmUri;
    Ringtone ringtone;
    UUID delayWorkId;
    String APP_NAME, ALARM_NAME;

    static String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.i("알람 액티비티", "onCreate()");

        // 레이아웃 설정
        setContentView(R.layout.alarm_dialog);
        // 액티비티 시작되면 알림 끄기
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(8338);
        // 액티비티 시작되면 WorkRequests 삭제
        String DELAY_WORK_ID = getIntent().getStringExtra("DELAY_WORK_ID");
        if (DELAY_WORK_ID != null && !DELAY_WORK_ID.isEmpty())
            delayWorkId = UUID.fromString(getIntent().getStringExtra("DELAY_WORK_ID"));
        if (delayWorkId != null) {
            android.util.Log.i("알람 액티비티",
                    "이전 워크가 삭제되었습니다 (" + delayWorkId.toString() + ")");
            WorkManager.getInstance(this).cancelWorkById(delayWorkId);
        }
        // 알람 설정 및 시작
        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
        // 권한 설정 확인 및 요청
        Boolean isCameraPermissionGranted = checkSelfPermission(PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
        Boolean isWriteStoragePermissionGranted = checkSelfPermission(PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
        if (isCameraPermissionGranted && isWriteStoragePermissionGranted) {
            android.util.Log.i("권한 설정", "완료");
        } else {
            android.util.Log.i("권한 설정", "요청");
            ActivityCompat.requestPermissions(AlarmNotifyActivity.this, PERMISSIONS, 1);
        }
        // 컴포넌트 설정
        ImageButton cameraButton = findViewById(R.id.alarm_camera_button);
        ImageButton delayButton = findViewById(R.id.alarm_delay_button);
        TextView title = findViewById(R.id.dialog_header_layout);
        APP_NAME = getString(R.string.app_name);
        ALARM_NAME = getIntent().getStringExtra("NAME");
        title.setText(APP_NAME + " - " + ALARM_NAME);
        cameraButton.setOnClickListener(view -> {
            // 알람 사운드 중지
            ringtone.stop();
            // 카메라 인텐트 실행
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    //e.printStackTrace();
                }

                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    android.util.Log.i("알람 액티비티", "cameraIntent photoUri: " + photoUri);

                    startActivityForResult(cameraIntent, 1);
                }
            }
        });
        delayButton.setOnClickListener(view -> {
            // Work request 생성
            WorkRequest delayWorkRequest =
                    new OneTimeWorkRequest.Builder(AlarmWorker.class)
                            .setInitialDelay(30, TimeUnit.MINUTES)
                            .addTag(ALARM_NAME)
                            .build();
            // WorkManager 큐에 추가
            delayWorkId = delayWorkRequest.getId();
            WorkManager.getInstance(getApplicationContext()).enqueue(delayWorkRequest);
            // 알람 사운드 중지
            ringtone.stop();
            // 알림 띄우기
            showNotification();
            // 액티비티 종료
            finish();
        });
    }

    final static String CHANNEL_ID = "AlarmNotification";

    private void showNotification() {
        Intent intent = new Intent(this, AlarmNotifyActivity.class);
        intent.putExtra("NAME", ALARM_NAME);
        intent.putExtra("DELAY_WORK_ID", delayWorkId.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("알람 이름 : " + ALARM_NAME)
                .setContentText("누르면 다시 알람창이 활성화됩니다")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(8338, builder.build());

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Alarm Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        assert manager != null;
        manager.createNotificationChannel(serviceChannel);
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
        // 카메라 이미지 작업
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 촬영 했을 때
            // 알람 사운드 중지
            ringtone.stop();

            // 액티비티 종료
            finish();
        } else {
            // 촬영 안하고 종료했을 때
            // 알람 사운드 다시 시작
            ringtone.play();
        }
    }


    private String imageFilePath;
    private String photoUri;

    private File createImageFile() throws IOException {
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();

        // DB에 저장
        Realm realm = MainViewModel.Photos.getRealm();
        realm.beginTransaction();
        PhotoObject photo = realm.createObject(PhotoObject.class);
        photo.setDATE(date);
        photo.setPHOTO_URI(imageFilePath);
        realm.commitTransaction();
        android.util.Log.i("알람 액티비티", "imageFilePath: " + imageFilePath + "\tphotoUri: " + photoUri);
        return image;
    }
}