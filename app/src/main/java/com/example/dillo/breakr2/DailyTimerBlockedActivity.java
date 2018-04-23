package com.example.dillo.breakr2;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.TextView;
        import static com.example.dillo.breakr2.MainActivity.appPackages;
        import static com.example.dillo.breakr2.MainActivity.appLabels;
        import static com.example.dillo.breakr2.MainActivity.appTimesAllowed;
        import static com.example.dillo.breakr2.MainActivity.appTimesSpentToday;

public class DailyTimerBlockedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.support.v7.app.ActionBar ac = getSupportActionBar();
        ac.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_timer_blocked);
        Intent intent;
        int indexOfApp = getIntent().getIntExtra("indexOfApp", 600);
        TextView textView = findViewById(R.id.textViewId);
        textView.setText("Sorry! Looks like you've used all " + appTimesAllowed.get(indexOfApp)/60 + " minutes of " + appLabels.get(indexOfApp) + "\nClick the icon below to go home");
    }
    public void launchMain(View v){
        Intent intent = new Intent(DailyTimerBlockedActivity.this , MainActivity.class);
        startActivity(intent);
    }

}
