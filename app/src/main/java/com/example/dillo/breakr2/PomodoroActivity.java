package com.example.dillo.breakr2;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.example.dillo.breakr2.MainActivity.pomodoroCounter;
import static com.example.dillo.breakr2.MainActivity.pomodoroOn;

public class PomodoroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
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

    public void onToggleClicked(View v) {
        if (pomodoroOn) {
            ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
            Toast.makeText(getBaseContext(), "Sorry youre blocked for another: " + pomodoroCounter, Toast.LENGTH_SHORT).show();
            if (toggleButton.getText() == toggleButton.getTextOff()){
                toggleButton.setChecked(true);
            }
            else {
                toggleButton.setChecked(false);
            }
        }
        else {
            pomodoroOn = true;
        }
    }

    public void onSettingButtonClicked(View v) {
        Intent launchIntent = new Intent();
        launchIntent.setComponent(new ComponentName("com.example.dillo.breakr2", "com.example.dillo.breakr2.SettingsPomoActivity"));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
    }

}
