package com.example.anchovyfeeder;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AlarmWorker extends Worker {
    private String dialogTitle;

    public AlarmWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        dialogTitle  = getTags().toArray()[1].toString();
    }

    @Override
    public Result doWork() {
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmNotifyActivity.class);
        alarmIntent.putExtra("NAME", dialogTitle);


        getApplicationContext().startActivity(alarmIntent);
;


        /*
            작업의 성공 여부를 알려준다
               Result.success(): 작업이 성공적으로 완료되었습니다.
               Result.failure(): 작업에 실패했습니다.
               Result.retry(): 작업에 실패했으며 재시도 정책에 따라 다른 시점에 시도되어야 합니다.
        */
        return Result.success();
    }

}
