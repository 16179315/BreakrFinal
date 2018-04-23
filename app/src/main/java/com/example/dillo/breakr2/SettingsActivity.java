package com.example.dillo.breakr2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void launchPomoSettings(View v){
        Intent intent = new Intent(SettingsActivity.this , SettingsPomoActivity.class);
        startActivity(intent);
    }
    public void launchDTSettings(View v){
        Intent intent = new Intent(SettingsActivity.this , SettingsTimerActivity.class);
        startActivity(intent);
    }
    public void launchReportBug(View v){
        Intent intent = new Intent(SettingsActivity.this , ReportBugActivity.class);
        startActivity(intent);
    }
}
