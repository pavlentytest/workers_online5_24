package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private OneTimeWorkRequest workRequest, workRequest2;
    private PeriodicWorkRequest periodicWorkRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Data data = new Data.Builder().putInt("key",123123).build();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();


        periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWork.class,15, TimeUnit.MINUTES).build();

        workRequest = new OneTimeWorkRequest.Builder(MyWork.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        workRequest2 = new OneTimeWorkRequest.Builder(MyWork.class).build();

        List<OneTimeWorkRequest> list = new ArrayList<>();
        list.add(workRequest);
        list.add(workRequest2);
        // параллельно
        //WorkManager.getInstance(this).enqueue(list);

        // последовательно
        WorkManager.getInstance(this).beginWith(workRequest).then(workRequest2).enqueue();

        //WorkManager.getInstance(this).enqueue(workRequest);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId()).observe(
                this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d("RRR","Status 1 = "+workInfo.getState());
                        Log.d("RRR","Data from worker = "+workInfo.getOutputData().getString("from"));
                    }
                }
        );
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest2.getId()).observe(
                this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d("RRR","Status 2 = "+workInfo.getState());
                    }
                }
        );

        // отмена
        WorkManager.getInstance(this).cancelWorkById(workRequest.getId());
    }
}