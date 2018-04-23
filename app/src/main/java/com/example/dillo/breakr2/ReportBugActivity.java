package com.example.dillo.breakr2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReportBugActivity extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);
        final EditText name   = (EditText)findViewById(R.id.nameId);
        final EditText bugReport   = (EditText)findViewById(R.id.bugDetailsId);
        final String _name = name.getText().toString();
        final String _bugReport = bugReport.getText().toString();

        Button button = (Button) findViewById(R.id.buttonId);
        button.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                StringBuilder body = new StringBuilder();
                body.append("Name: "+name.getText().toString());
                body.append("\nBug Report: "+bugReport.getText().toString());

                // Sending to admin
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[] {"patrick55529@gmail.com"} );
                i.putExtra(Intent.EXTRA_SUBJECT, "BUG REPORT: BREAKR");
                i.putExtra(Intent.EXTRA_TEXT, body.toString());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReportBugActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void launchHome(View v){
        Intent intent = new Intent(ReportBugActivity.this , MainActivity.class);
        startActivity(intent);
    }
}