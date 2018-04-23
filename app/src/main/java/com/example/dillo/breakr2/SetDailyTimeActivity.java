package com.example.dillo.breakr2;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import static com.example.dillo.breakr2.MainActivity.appLabels;
import static com.example.dillo.breakr2.MainActivity.appTimesAllowed;
import static com.example.dillo.breakr2.MainActivity.pomodoroCounter;
import static com.example.dillo.breakr2.MainActivity.saveFile;
import static com.example.dillo.breakr2.MainActivity.appPackages;
import static com.example.dillo.breakr2.MainActivity.appTimesSpentToday;
import static com.example.dillo.breakr2.MainActivity.timeTillResetCounter;

public class SetDailyTimeActivity extends AppCompatActivity {
    TimePicker timePicker1;
    int indexOfApp;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_daily_time);
        String appName = getIntent().getStringExtra("appName");
        indexOfApp = appLabels.indexOf(appName);
        Log.v("index Of:" , appLabels.get(indexOfApp));
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        int timeAllowedCurrent = appTimesAllowed.get(indexOfApp);
        if(timeAllowedCurrent != 86401) {
            timePicker1.setHour(timeAllowedCurrent / 3600);
            timePicker1.setMinute(timeAllowedCurrent / 60);
        }
        else {
            timePicker1.setHour(0);
            timePicker1.setMinute(0);
        }
        TextView textView = findViewById(R.id.textView2);
        textView.setText("Change the time allowed daily for " + appName + " in hours and minutes");
        TextView textView2 = findViewById(R.id.textViewDetailsId);

        if(appTimesAllowed.get(indexOfApp) != 86401) {
            textView2.setText("You have used " + (appTimesSpentToday.get(indexOfApp) / 60) + " minutes out of todays possible " + appTimesAllowed.get(indexOfApp) / 60);
        }
        else {
            textView2.setText("");
        }

        final TextView textView3 = findViewById(R.id.textView3Id);
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                textView3.setText("Time till reset: " + timeTillResetCounter);
                h.postDelayed(this, delay);
            }
        }, delay);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveTime(View v) {
        int hour = timePicker1.getHour();
        int minute = timePicker1.getMinute();
        hour = (hour*60)*60;
        minute = minute*60;
        int totalSeconds = hour + minute;
        appTimesAllowed.set(indexOfApp,totalSeconds);
        saveFile();

        Toast.makeText(SetDailyTimeActivity.this, "Changed the time allowed for " + appLabels.get(indexOfApp), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SetDailyTimeActivity.this, SettingsTimerActivity.class);
        startActivity(intent);
    }
}
