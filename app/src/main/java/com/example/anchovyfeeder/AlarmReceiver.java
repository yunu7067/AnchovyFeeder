package com.example.anchovyfeeder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {


        context.startActivity(new Intent(context.getApplicationContext(), AlarmNotifyActivity.class));
/*

        try {
            intent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            pi.send();


            AlarmDialog alarmDialog = new AlarmDialog(context.getApplicationContext());
            alarmDialog.show();


        } catch (PendingIntent.CanceledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/


        NotificationManager notifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(context, MainActivity.class);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

        NotificationCompat.Builder notifyCompatBuilder = new NotificationCompat.Builder(context, "default");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyCompatBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);

            String channelName = "반복 알람 채널";
            String description = "설정된 시간에 알람이 반복됩니다.";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notifyCannel = new NotificationChannel("default", channelName, importance);
            notifyCannel.setDescription(description);

            if (notifyManager != null) {
                notifyManager.createNotificationChannel(notifyCannel);
            }

        } else {
            notifyCompatBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        notifyCompatBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen((System.currentTimeMillis()))
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);

        if (notifyManager != null) {
            notifyManager.notify(1234, notifyCompatBuilder.build());

            Calendar nextNotifyTime = Calendar.getInstance();

            nextNotifyTime.add(Calendar.DATE, 1);

            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            editor.apply();

            Date currentDateTime = nextNotifyTime.getTime();
            String data_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(), "다음 알람은 " + data_text + "으로 설정되었습니다.", Toast.LENGTH_SHORT).show();

        }


    }
}
