package com.example.dillo.breakr2;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.os.Process;
import android.view.Window;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    public Context context;
    public static int pomodoroSeconds=30;
    public static int pomodoroSavedSeconds=30;
    public static boolean pomodoroOn=false;
    public static String pomodoroCounter="";
    public static ArrayList<String> pomodoroApps = new ArrayList<String>();
    public static final String ACTION_START_SERVICE = "com.example.dillo.breakr2.MonitorService.ACTION_START_SERVICE";
    public static ArrayList<String> appPackages = new ArrayList<>();
    public static ArrayList<String> appLabels = new ArrayList<>();
    public static ArrayList<Integer> appTimes = new ArrayList<>();
    public static ArrayList<Integer> appTimesAllowed = new ArrayList<>();
    public static ArrayList<Integer> appTimesSpentToday = new ArrayList<>();
    public static ArrayList<Drawable> appIconsDrawable = new ArrayList<>();
    public static String [] temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!(checkForPermission())) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        setTheme(R.style.AppTheme);
        /* hdie action bar not working really
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_main);
        */
        android.support.v7.app.ActionBar ac = getSupportActionBar();
        ac.hide();

        Intent startIntent = new Intent(getApplicationContext(), MonitorService.class);
        startIntent.setAction(ACTION_START_SERVICE);
        startService(startIntent);

        File mainFile = getFileStreamPath("data.txt");
        if (mainFile.length() == 0) {
            setupArrayLists();
            saveFile();
            readFile();
        } else {
            readFile();
        }
        File pomodoroFile = getFileStreamPath("dataPomo.txt");
        if (pomodoroFile.length() == 0) {
            saveFilePomodoro();
            readFilePomodoro();
        } else {
            readFilePomodoro();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private boolean checkForPermission() {
        UsageStatsManager usm=(UsageStatsManager)getSystemService("usagestats");
        Calendar calendar=Calendar.getInstance();
        long toTime=calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR,-1);
        long fromTime=calendar.getTimeInMillis();
        final List<UsageStats> queryUsageStats=usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,fromTime,toTime);
        boolean granted=queryUsageStats!=null&&queryUsageStats!= Collections.EMPTY_LIST;
        return(granted);
    }

    private void setupArrayLists() {
        //Get a list of all apps
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        List<PackageInfo> apps2 = pm.getInstalledPackages((PackageManager.GET_META_DATA));
        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();

        //refine list to user installed or system apps
        for (int i=0;i<apps.size();i++) {
            //checks for flags; if flagged, check if updated system app
            if ((apps.get(i).flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {

                //it's a system app, not interested
            } else if ((apps.get(i).flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                //in this case, it should be a user-installed app
            }
            else if("com.example.dillo.breakr2" == apps2.get(i).packageName){

                //if our app do nothing
            }
            else {
                Log.v("Tet",apps2.get(i).packageName);
                installedApps.add(apps.get(i));
                appPackages.add(apps2.get(i).packageName);
                try {
                    appIconsDrawable.add(getPackageManager().getApplicationIcon(apps2.get(i).packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

        //Setups a list with the names of apps as Strings
        for (ApplicationInfo app : installedApps) {
            appLabels.add((String) pm.getApplicationLabel(app));
            appTimesAllowed.add(86401);
            appTimesSpentToday.add(0);
        }
    }

    public void launchPomodoro(View v){
        Intent intent = new Intent(MainActivity.this , PomodoroActivity.class);
        startActivity(intent);
    }

    public void launchSettings(View v){
        Intent intent = new Intent(MainActivity.this , SettingsActivity.class);
        startActivity(intent);
    }

    public void launchHistory(View v){
        Intent intent = new Intent(MainActivity.this , HistoryActivity.class);
        startActivity(intent);
    }

    public void launchReportBug(View v){
        Intent intent = new Intent(MainActivity.this , ReportBugActivity.class);
        startActivity(intent);
    }


    public static void saveFile(){
        File file = new File("data/data/com.example.dillo.breakr2/data.txt");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter output = new BufferedWriter(fw);

            for (int i = 0; i < appPackages.size(); i++){
                output.write(appPackages.get(i) + "," + appTimesSpentToday.get(i) + "," + appTimesAllowed.get(i));
                output.newLine();
            }

            output.close();

        } catch (Exception e) {
            Log.v("FILE", "I cannot create that file");
        }
    }

    public static void readFile() {
        File file = new File("data/data/com.example.dillo.breakr2/data.txt");
        //Read text from file
        String[] lineArr = new String[3];
        appPackages.clear();
        appTimesSpentToday.clear();
        appTimesAllowed.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                lineArr = line.split(",");
                appPackages.add(lineArr[0]);
                appTimesSpentToday.add(Integer.valueOf(lineArr[1]));
                appTimesAllowed.add(Integer.valueOf(lineArr[2]));
            }
            br.close();
        }
        catch (Exception e){
            String error="";
            error=e.getMessage();
            Log.v("FILE",error);
        }
    }


    public static void saveFilePomodoro(){
        File file = new File("data/data/com.example.dillo.breakr2/dataPomo.txt");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter output = new BufferedWriter(fw);

            for (int i = 0; i < pomodoroApps.size(); i++){
                output.write(appPackages.get(i));
                output.newLine();
            }
            output.close();

        } catch (Exception e) {
            Log.v("FILE", "I cannot create that file");
        }
    }

    public void readFilePomodoro() {

        File file = new File("data/data/com.example.dillo.breakr2/dataPomo.txt");
        //Read text from file
        pomodoroApps.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                pomodoroApps.add(line);
            }
            br.close();
        }
        catch (Exception e){
            String error="";
            error=e.getMessage();
            Log.v("FILE",error);
        }
    }

}
