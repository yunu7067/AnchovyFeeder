package com.example.anchovyfeeder;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

public class AlarmWorker extends Worker {
    private static final String KEY_INPUT_URL = "KEY_INPUT_URL";
    private static final String KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME";
    private String dialogTitle;
    private Boolean isDialogAlive;
    private NotificationManager notificationManager;

    public AlarmWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        dialogTitle = getTags().toArray()[1].toString();
        notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        isDialogAlive = false;
    }

    @Override
    public Result doWork() {
        android.util.Log.i("알람 워커", "doWork() - " + dialogTitle);
        try {
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmNotifyActivity.class);
            alarmIntent.putExtra("NAME", dialogTitle);
            getApplicationContext().startActivity(alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK ));

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();


            return Result.failure();
        }
        /*
            작업의 성공 여부를 알려준다
               Result.success(): 작업이 성공적으로 완료되었습니다.
               Result.failure(): 작업에 실패했습니다.
               Result.retry(): 작업에 실패했으며 재시도 정책에 따라 다른 시점에 시도되어야 합니다.
        */
    }


}
