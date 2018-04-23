package com.example.dillo.breakr2;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import static com.example.dillo.breakr2.MainActivity.pomodoroSavedSeconds;
import static com.example.dillo.breakr2.MainActivity.pomodoroSeconds;

public class SetPomodoroTimeActivity extends AppCompatActivity {
    TimePicker timePicker1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pomodoro_time);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        timePicker1.setHour(pomodoroSavedSeconds/3600);
        timePicker1.setMinute(pomodoroSavedSeconds/60);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveTime(View v) {
        int hour = timePicker1.getHour();
        int minute = timePicker1.getMinute();
        hour = (hour*60)*60;
        minute = minute*60;
        int totalSeconds = hour + minute;
        pomodoroSavedSeconds = totalSeconds;
        pomodoroSeconds = pomodoroSavedSeconds;
        Intent intent = new Intent(SetPomodoroTimeActivity.this, SettingsPomoActivity.class);
        startActivity(intent);
    }
}
