package com.example.dillo.breakr2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.dillo.breakr2.MainActivity.pomodoroApps;
import static com.example.dillo.breakr2.MainActivity.appLabels;
import static com.example.dillo.breakr2.MainActivity.appPackages;
import static com.example.dillo.breakr2.MainActivity.pomodoroCounter;
import static com.example.dillo.breakr2.MainActivity.pomodoroOn;
import static com.example.dillo.breakr2.MainActivity.saveFilePomodoro;

import java.util.ArrayList;
import java.util.List;

public class SettingsPomoActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_pomo);
        setupList1();
        setupList2();
    }

    private void setupList1() {

        //Setup adapter for the list :installedappstrings
        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                appLabels
        );

        //Connect list view to adapter
        ListView listView = (ListView) findViewById(R.id.lv1);
        listView.setAdapter(adapter1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView) view).getText().toString();
                selectedList1(item);

            }
        });
    }
    private void setupList2() {
        //Get a list of all apps

        //Setup adapter for the list :pomodoroApps
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                pomodoroApps
        );

        //Connect list view to adapter
        ListView listView = (ListView) findViewById(R.id.lv2);
        listView.setAdapter(adapter2);

    }
    public void selectedList1(String item) {
            if (pomodoroApps.contains(item)) {
                String appPackage = appPackages.get(appLabels.indexOf(item));
                pomodoroApps.remove(pomodoroApps.indexOf(appPackage));
                Toast.makeText(getBaseContext(), "Removed " + item, Toast.LENGTH_SHORT).show();
            } else {
                String appPackage = appPackages.get(appLabels.indexOf(item));
                pomodoroApps.add(appPackage);
                Toast.makeText(getBaseContext(), "Added " + item, Toast.LENGTH_SHORT).show();
            }

            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
            saveFilePomodoro();

    }

    public void launchPomodorTime(View v){
        Intent intent = new Intent(SettingsPomoActivity.this, SetPomodoroTimeActivity.class);
        startActivity(intent);
    }
    public void clear(View v) {
        if(pomodoroOn) {
            Toast.makeText(getBaseContext(), "Sorry youre blocked for another: " + pomodoroCounter, Toast.LENGTH_SHORT).show();
        }
        else {
            pomodoroApps.clear();
            adapter2.notifyDataSetChanged();
            saveFilePomodoro();
        }
    }
}