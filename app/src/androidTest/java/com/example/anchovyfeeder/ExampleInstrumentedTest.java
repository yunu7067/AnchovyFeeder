package com.example.anchovyfeeder;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.BackoffPolicy;
import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                context, config);
    }
    @Test
    public void testPeriodicWork() throws Exception {
        System.out.println("알람 test");
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Create request
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(AlarmWorker.class, 15, TimeUnit.MINUTES)
                        // 작업을 식별하는데 사용하는 태그 지정
                        .addTag("테그")
                        // 입력 데이터 할당. 값은 Data 객체에 Key-Value 쌍으로 저장된다
                        //.setInputData(mData)
                        // 특정 시간에 시작하는 함수가 없으므로 지연을 사용하여 시작 시간을 설정해야 한다.
                        .setInitialDelay(5, TimeUnit.SECONDS)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                30,
                                TimeUnit.MINUTES
                        )
                        .build();

        WorkManager workManager = WorkManager.getInstance(context);
        TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver(context);
        // Enqueue
        workManager.enqueue(request).getResult().get();
        // Tells the testing framework the period delay is met
        testDriver.setPeriodDelayMet(request.getId());
        // Get WorkInfo and outputData
        WorkInfo workInfo = workManager.getWorkInfoById(request.getId()).get();
        // Assert
        assertThat(workInfo.getState(), is(WorkInfo.State.ENQUEUED));
    }
}