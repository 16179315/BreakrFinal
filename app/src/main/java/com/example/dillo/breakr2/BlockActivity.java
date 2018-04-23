package com.example.dillo.breakr2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.example.dillo.breakr2.MainActivity.pomodoroCounter;

public class BlockActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.support.v7.app.ActionBar ac = getSupportActionBar();
        ac.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        final TextView textView;
        textView = (TextView) findViewById(R.id.textView3);
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                textView.setText(pomodoroCounter);
                h.postDelayed(this, delay);
            }
        }, delay);
    }
    public void launchPhone(View v){
        if (ContextCompat.checkSelfPermission(BlockActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BlockActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
        }
        else {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, null);
            startActivity(callIntent);
        }
    }
    public void launchSMS(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP;
        intent.setFlags(flags);
        Context context = this.getApplicationContext();
        context.startActivity(intent);
    }
}
