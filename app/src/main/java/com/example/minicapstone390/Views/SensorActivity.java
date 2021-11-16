package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.Models.Sensor;
import com.example.minicapstone390.Models.SensorData;
import com.example.minicapstone390.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

// DateTime
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Objects;

public class SensorActivity extends AppCompatActivity {
    private static final String TAG = "SensorActivity";

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected LineChart sensorChart;
    protected TextView chartTitle;
    protected RadioGroup graphTimesOptions;
    protected Toolbar toolbar;
    protected String sensorId;
    protected String function;

    public double total = 0;
    public int graphTimeScale = 7;
    public ArrayList<Double> sensorValues = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(SensorActivity.this);
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        graphTimesOptions = (RadioGroup) findViewById(R.id.graphTimeOptions);
        graphTimesOptions.check(R.id.weekButton);

        chartTitle = (TextView) findViewById(R.id.chart_title);
        sensorChart = (LineChart) findViewById(R.id.sensorChart);
        // Disable legend and description
        sensorChart.getLegend().setEnabled(false);
        sensorChart.getDescription().setEnabled(false);

        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            sensorId = carryOver.getString("sensorId");
            function = carryOver.getString("editDialog", "");
            System.out.println(function);
            if(function.equals("editSensor()")) {
                editSensor(sensorId);
            }
            if (sensorId != null) {
                displaySensorInfo(sensorId);
                getAllSensorData();
//                setGraphScale();
            } else {
                Log.e(TAG, "Id is null");
            }
        } else {
            Toast.makeText(this, "Error fetching device", Toast.LENGTH_LONG).show();
            openHomeActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        displaySensorInfo(sensorId);
        setGraphScale();
    }

    private void editSensor(String sensorId) {
        Bundle bundle = new Bundle();
        bundle.putString("id", sensorId);
        SensorFragment dialog = new SensorFragment();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SensorFragment");
    }

    // Display basic info of the sensor
    private void displaySensorInfo(String sensorId) {
        DatabaseReference sensorRef = dB.getSensorChild(sensorId);

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chartTitle.setText(getResources().getString(R.string.sensor_graph).replace("{0}", Objects.requireNonNull(snapshot.child("SensorName").getValue(String.class))));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
    }

    // Set the graph scale when button is selected
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setGraphScale() {
        graphTimesOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.dayButton:
                        graphTimeScale = 0;
                        break;
                    case R.id.weeksButton:
                        graphTimeScale = 14;
                        break;
                    case R.id.monthButton:
                        graphTimeScale = 28;
                        break;
                    default:
                        graphTimeScale = 7;
                }
            }
        });
    }

    // TODO: Fix spaghetti
    // Get the time scale of the X axis of the graph
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<LocalDateTime> updateGraphDates() {
        List<LocalDateTime> history = new ArrayList<>();
        setGraphScale();
        long decrement = graphTimeScale / 7;
        if (decrement == 0) {
            for (long i = 23; i >= 0; i -= 4) {
                history.add(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 0)).minusHours(i));
            }
            history.add(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)));
        } else {
            for (long i = graphTimeScale; i >= 0; i -= decrement) {
                history.add(LocalDateTime.now().minusDays(i));
            }
        }
        System.out.println(history);
        return new ArrayList<>(history);
//        getAllSensorData(history);
    }

    public void getAllSensorData() {
        ArrayList<SensorData> validData = new ArrayList<>();
        dB.getSensorChild(sensorId).child("SensorPastValues").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Double> values = new ArrayList<>();
                ArrayList<LocalDateTime> times = new ArrayList<>();
                ArrayList<LocalDateTime> history = updateGraphDates();
                LocalDateTime start = history.get(0);
                LocalDateTime end = history.get(history.size() - 1);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocalDateTime time = LocalDateTime.parse(ds.getKey(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    System.out.println("Time" + time.toString());
                    System.out.println("Start" + start.toString());
                    System.out.println("End" + end.toString());
                    if (start.isBefore(time) && end.isAfter(time)) {
                        values.add(ds.child("Value").getValue(Double.class));
                        times.add(LocalDateTime.parse(ds.getKey(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                }
                validData.add(new SensorData(values, times));
                System.out.println("Size" + validData.get(0).getValues().size());
                producer(history, validData.get(0));
                validData.clear();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void producer(List<LocalDateTime> history, SensorData data) {
        if (data.getValues().size() != 0) {
            LocalDateTime start = history.get(0);
            LocalDateTime end = history.get(history.size() - 1);
            long duration = Duration.between(start, end).getSeconds();
            long cuts = data.getValues().size();
            long delta = duration / (cuts - 1);
            ArrayList<String> results = new ArrayList<>();

            for (int i = 0; i < cuts; i++) {
                results.add(start.plusSeconds(i * delta).format(DateTimeFormatter.ofPattern("MM/dd")));
            }
            setXAxisLabels(history, data, results);
        }
    }

    // Setting LineChart
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setXAxisLabels(List<LocalDateTime> history, SensorData data, ArrayList<String> results) {
        ArrayList<String> xAxisLabel = new ArrayList<>(results.size());
        xAxisLabel.addAll(results);

        XAxis xAxis = sensorChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(history.size() + 1,true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < results.size()) {
                    return xAxisLabel.get((int) value);
                } else {
                    return "";
                }
            }
        });
        setYAxis();
        setData(data, results);
    }

    private void setYAxis() {
        YAxis leftAxis = sensorChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1.1f);
        leftAxis.setGranularity(0.1f);

        YAxis rightAxis = sensorChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // TODO: Fix spaghetti
    protected void setData(SensorData data, ArrayList<String> results) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int x = 1; x < results.size() - 1; x++) {
            values.add(new Entry(x, data.getValues().get(x).floatValue()));
        }
        LineDataSet set = new LineDataSet(values, "Test");
        set.setDrawValues(false);
        set.setLineWidth(2);

        LineData lineData = new LineData(set);
        lineData.setValueTextColor(Color.BLACK);
        lineData.setValueTextSize(9f);

        sensorChart.setData(lineData);

        sensorChart.invalidate();
    }

    private void notification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "id").setContentTitle("Notif").setContentText("Over 10").setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensor_menu, menu);
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.disable_sensor) {
//            disableSensor();
        }
        return super.onOptionsItemSelected(item);
    }


    // TODO: Add status to DB
    private void disableSensor() {
        dB.getSensorChild(sensorId).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue(Boolean.class)) {
                        dB.getSensorChild(sensorId).child("status").setValue(false);
                    } else {
                        dB.getSensorChild(sensorId).child("status").setValue(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                    throw e;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
    }

    // Navigate back to Home Activity
    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Navigate back to device page on task-bar return
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
