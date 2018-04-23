package com.example.dillo.breakr2;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Intent.ACTION_MAIN;
import static com.example.dillo.breakr2.MainActivity.ACTION_START_SERVICE;
import static com.example.dillo.breakr2.MainActivity.pomodoroSeconds;
import static com.example.dillo.breakr2.MainActivity.pomodoroSavedSeconds;
import static com.example.dillo.breakr2.MainActivity.pomodoroOn;
import static com.example.dillo.breakr2.MainActivity.pomodoroCounter;
import static com.example.dillo.breakr2.MainActivity.timeTillResetCounter;
import static com.example.dillo.breakr2.MainActivity.pomodoroApps;
import static com.example.dillo.breakr2.MainActivity.appLabels;
import static com.example.dillo.breakr2.MainActivity.appPackages;
import static com.example.dillo.breakr2.MainActivity.appTimesAllowed;
import static com.example.dillo.breakr2.MainActivity.appTimesSpentToday;
import static com.example.dillo.breakr2.MainActivity.saveFile;
import static com.example.dillo.breakr2.MainActivity.readFile;


public class MonitorService extends Service {
    private static final String TAG = "Monitor service *****";
    public static boolean isServiceRunning = false;
    static final int NOTIFICATION_ID = 543;
    public final String ACTION_MAIN =  "com.example.dillo.breakr2.MonitorService.ACTION_MAIN";
    private Handler handler;
    Runnable runnable;
    Context context = this;
    NotificationChannel channel;
    public static int daySeconds= 0;
    int readFileTimer = 180;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        initChannels(context);
        startServiceWithNotification();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void run() {

                        String packageName = getForegroundTask();
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                        List<ActivityManager.RunningTaskInfo> taskInfo = am
                                .getRunningTasks(1);

                        ComponentName componentInfo = taskInfo.get(0).topActivity;
                        //String packageName = componentInfo.getPackageName();
                        String[] packageArr = packageName.split("\\.");
                        String name = packageArr[packageArr.length - 1].toLowerCase();

                        if(pomodoroOn) {
                            pomodoroSeconds--;
                            int hours = pomodoroSeconds / 3600;
                            int minutes = (pomodoroSeconds % 3600) / 60;
                            int seconds = pomodoroSeconds % 60;

                            pomodoroCounter = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                            if(pomodoroApps.contains(packageName)) {
                                Intent launchIntent = new Intent();
                                launchIntent.setComponent(new ComponentName("com.example.dillo.breakr2", "com.example.dillo.breakr2.BlockActivity"));
                                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(launchIntent);
                            }

                        }
                        if(pomodoroSeconds == 0 && pomodoroOn) {
                            pomodoroSeconds = pomodoroSavedSeconds;
                            pomodoroOn = false;
                        }
                        ///Deal with the daily timer stuff
                        int indexOfApp = 0;
                        if(appPackages.contains(packageName)){
                            indexOfApp = appPackages.indexOf(packageName);

                            appTimesSpentToday.set(indexOfApp, appTimesSpentToday.get(indexOfApp) + 1);
                            Log.v("TEST", appTimesSpentToday.get(indexOfApp) + " " + appLabels.get(indexOfApp) + "time allowed: " + appTimesAllowed.get(indexOfApp));
                            if (appTimesAllowed.get(indexOfApp) < appTimesSpentToday.get(indexOfApp)) {
                                Intent launchIntent = new Intent();
                                launchIntent.setComponent(new ComponentName("com.example.dillo.breakr2", "com.example.dillo.breakr2.DailyTimerBlockedActivity"));
                                launchIntent.putExtra("indexOfApp", indexOfApp);
                                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(launchIntent);
                            }
                        }

                        //reset time after one day
                        daySeconds++;
                        if(daySeconds >86400) {
                            appTimesSpentToday.clear();
                            for(int i = 0;i<appPackages.size();i++) {
                                appTimesSpentToday.add(0);
                                saveFile();
                            }
                        }

                        int total = 86400-daySeconds;
                        int hours = total / 3600;
                        int minutes = (total % 3600) / 60;
                        int seconds = total % 60;
                        timeTillResetCounter = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        //Raed in file every 3 minutes
                        readFileTimer--;
                        if(readFileTimer<0) {
                            readFile();
                            readFileTimer = 180;
                        }
                    }

                }).start();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(ACTION_START_SERVICE)) {
            startServiceWithNotification();
        }
        else stopMyService();
        return START_STICKY;
    }

    void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(ACTION_MAIN);  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_breakr);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                     Notification notification = new Notification.Builder(context, "2")
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.app_name))
                    .setContentText("Your monitor service is running as expected")
                    .setSmallIcon(R.mipmap.ic_breakr)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(contentPendingIntent)
                    .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                    .build();
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
            startForeground(NOTIFICATION_ID, notification);
        }
        else {
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.app_name))
                    .setContentText("Your monitor service is running as expected")
                    .setSmallIcon(R.mipmap.ic_breakr)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(contentPendingIntent)
                    .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                    .build();
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    private String getForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        return(currentApp);
    }

    private void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        Log.v("Got in here boi","noice");
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        channel = new NotificationChannel("2",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
    }
}


