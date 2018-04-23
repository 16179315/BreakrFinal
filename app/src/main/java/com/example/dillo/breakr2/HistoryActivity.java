package com.example.dillo.breakr2;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HistoryActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    Context context;
    List<UsageStats> usageStats;
    Map<String, UsageStats> usageStatsAggregate;
    ArrayList<String> packageNames = new ArrayList<>();
    ArrayList<Long> timeInForeground = new ArrayList<>();
    ArrayList<String> packageNamesArrayList = new ArrayList<>();
    ArrayList<String> applicationNamesArrayList = new ArrayList<>();
    ArrayList<Long> timeInForegroundArrayList = new ArrayList<>();
    ArrayList<PieEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();
    Spinner spinner;
    Boolean pieChartLayoutBoolean = true;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_history);
        setContentView(R.layout.activity_history);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerId);



        Button mButton = (Button) findViewById(R.id.buttonId);
        final EditText mEdit = (EditText) findViewById(R.id.numberId);
        final PieChart piechart = (PieChart) findViewById(R.id.idPieChart);

        piechart.setUsePercentValues(true);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5,10,5,5);

        piechart.setDragDecelerationFrictionCoef(0.95f);

        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);
        piechart.spin(1500, 0, 460, Easing.EasingOption.EaseInOutQuad);
        piechart.setEntryLabelColor(Color.BLACK);


        context = getApplicationContext();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();
        usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, start, end);
        usageStatsAggregate = usageStatsManager.queryAndAggregateUsageStats(start, end);
        Set entries = usageStatsAggregate.entrySet();
        Iterator iterator = entries.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry mapping = (Map.Entry) iterator.next();
            packageNames.add(mapping.getKey().toString());
            timeInForeground.add(((UsageStats) mapping.getValue()).getTotalTimeInForeground());
            Log.v("Package name", packageNames.get(i));
            Log.v("Time", String.valueOf(timeInForeground.get(i)));
            i++;
        }
        findTopApps(5);
        addDataSet(piechart);
        piechart.animateY(1400,Easing.EasingOption.EaseInOutCubic);
        addDataSet(piechart);
        piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d("d",e.toString());
                Log.d("d",h.toString());
            }

            @Override
            public void onNothingSelected() {
            }
        });
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        timeInForegroundArrayList.clear();
                        packageNamesArrayList.clear();
                        int numberOfApps = Integer.valueOf(mEdit.getText().toString());
                        findTopApps(numberOfApps);
                        addDataSet(piechart);
                        piechart.animateY(1000,Easing.EasingOption.EaseInOutCubic);
                       // piechart.spin(1500, 0, 460, Easing.EasingOption.EaseInOutQuad);
                    }
                });

    }
///////////////////////////////////////////////////////////////////////////////////
    private void addDataSet(PieChart pieChart) {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        for(int i=0;i<timeInForegroundArrayList.size();i++) {
            yEntrys.add(new PieEntry(timeInForegroundArrayList.get(i),applicationNamesArrayList.get(i)));
            xEntrys.add(applicationNamesArrayList.get(i));
        }
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"The most used apps on your phone");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueTextColor(Color.YELLOW);



        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        Legend legend = pieChart.getLegend();
        pieChart.getLegend().setWordWrapEnabled(true);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void findTopApps(int numberOfApps) {
        boolean returned = false;
        timeInForegroundArrayList.clear();
        packageNamesArrayList.clear();
        for(int i = 0;i<numberOfApps;i++) {
            timeInForegroundArrayList.add((long) 0);
            packageNamesArrayList.add("");
        }
        for (int i = 0; i < timeInForeground.size(); i++) {
            returned = false;
            for (int j = 0; j < numberOfApps && !returned; j++) {
                if (timeInForeground.get(i) > timeInForegroundArrayList.get(j)) {
                    if (!(packageNames.get(i).equals("com.google.android.apps.nexuslauncher") || packageNames.get(i).equals("com.android.launcher3"))) {
                        timeInForegroundArrayList.set(j,timeInForeground.get(i));
                        packageNamesArrayList.set(j,packageNames.get(i));
                        returned = true;
                        j=0;
                    }
                }
            }
        }
        setupAppNamesArray();
    }





    private List<LegendEntry> setupLegend() {
        setupAppNamesArray();
        List<LegendEntry> entries = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();
        colours.add(Color.GREEN);
        colours.add(Color.RED);
        colours.add(Color.CYAN);
        colours.add(Color.MAGENTA);
        colours.add(Color.YELLOW);
        int j = 0;
        for (int i = 0; i < packageNamesArrayList.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = ColorTemplate.JOYFUL_COLORS[i];
            entry.label = applicationNamesArrayList.get(i);
            entries.add(entry);
            j++;
            if(j == 7){
                j = 0;
            }
        }

        return(entries);
    }

    private void setupAppNamesArray() {
        applicationNamesArrayList.clear();
        for(int i =0;i<packageNamesArrayList.size();i++) {
            applicationNamesArrayList.add("");
            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageNamesArrayList.get(i), 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            applicationNamesArrayList.set(i,(String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)"));
        }
    }

    private void setupSpinner(Spinner spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chartsArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}

