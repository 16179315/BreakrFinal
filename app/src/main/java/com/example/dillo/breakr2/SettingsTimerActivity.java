package com.example.dillo.breakr2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import static com.example.dillo.breakr2.MainActivity.appIconsDrawable;
import static com.example.dillo.breakr2.MainActivity.appLabels;
import static com.example.dillo.breakr2.MainActivity.appPackages;
import static com.example.dillo.breakr2.MainActivity.appTimes;
import static com.example.dillo.breakr2.MainActivity.appTimesAllowed;
import static com.example.dillo.breakr2.MainActivity.appTimesSpentToday;
import static com.example.dillo.breakr2.MainActivity.pomodoroApps;


public class SettingsTimerActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_timer);
        //setupList1();
        String[] nameArray = new String[appPackages.size()];
        Drawable[] imageArray = new Drawable[appPackages.size()];
        String[] times = new String[appPackages.size()];

        for (int i = 0; i < nameArray.length; i++) {
            nameArray[i] = appLabels.get(i);
            imageArray[i] = appIconsDrawable.get(i);
            times[i] = "%" + String.valueOf(appTimesSpentToday.get(i)/appTimesAllowed.get(i));
        }

        final ListView listView;

        CustomListAdapter whatever = new CustomListAdapter(this, nameArray, imageArray, times);

        listView = (ListView) findViewById(R.id.listviewID);
        listView.setAdapter(whatever);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView) view.findViewById(R.id.NameTextViewID)).getText().toString();
                selectedList1(item);
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG);

            }
        });
    }

    public void selectedList1(String item) {
        Log.v("app label",item);
        launchDailyTime(item);
    }
    public void launchDailyTime(String item){
        Intent intent = new Intent(SettingsTimerActivity.this, SetDailyTimeActivity.class);
        intent.putExtra("appName", item);
        startActivity(intent);
    }

}

