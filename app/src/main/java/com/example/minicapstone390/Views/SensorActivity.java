package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// DateTime
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SensorActivity extends AppCompatActivity {

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected LineChart sensorChart;
    protected TextView sensorName, chartTitle;
    protected List<String> graphTime;
    protected Toolbar toolbar;
    protected String sensorId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        sensorName = (TextView) findViewById(R.id.sensor_name);
        chartTitle = (TextView) findViewById(R.id.chart_title);

        graphTime = dateHistoryList(LocalDate.now() ,sharePreferenceHelper.getGraphLength());
        System.out.println(graphTime);
        sensorChart = (LineChart) findViewById(R.id.sensorChart);
        // setData(sensorChart)
        // configureGraph(sensorChart)

        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            sensorId = carryOver.getString("sensorId");
            displaySensorInfo(sensorId);
        } else {
            Toast.makeText(this, "Error fetching device", Toast.LENGTH_LONG).show();
            openHomeActivity();
        }

        getSensorData();
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
            disableSensor();
        }
        return super.onOptionsItemSelected(item);
    }

    private void disableSensor() {
        dB.getSensorChild(sensorId).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Boolean.class)) {
                    dB.getSensorChild(sensorId).child("status").setValue(false);
                } else {
                    dB.getSensorChild(sensorId).child("status").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
                System.out.println(error.toString());
            }
        });
    }

    private void getSensorData() {
        dB.getSensorChild(sensorId).child("SensorPastValues").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pair<String, Double>> sensorData = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println(ds.getKey());
                    // Gets the date
                    System.out.println(LocalDate.parse(ds.getKey(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    // Gets the time of day
                    System.out.println(LocalTime.parse(ds.getKey(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    Instant instant = Instant.parse(ds.getKey()+".521Z");
                    Date time = null;
                    try {
                        time = Date.from(instant);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    System.out.println(time);
                    System.out.println(ds.child("Value").getValue(Double.class).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }

    private void displaySensorInfo(String sensorId) {
        DatabaseReference sensorRef = dB.getSensorChild(sensorId);

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sensorName.setText(snapshot.child("SensorName").getValue(String.class));
                chartTitle.setText(getResources().getString(R.string.sensor_graph).replace("{0}", Objects.requireNonNull(snapshot.child("SensorName").getValue(String.class))));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<String> dateHistoryList(LocalDate current, long length) {
        List<String> history = new ArrayList<>();
        length = 60;
        long decrement = 1;
        if (length == 0) {
            // split into hours
        } else {
            // split into days depending on formula
            decrement = length / 7;
        }

        // TODO
        switch ((int) decrement) {
            case 7:
                // return a week graph
                return Collections.emptyList();
            case 14:
                // return a 2 week graph
                return Collections.emptyList();
            case 30:
                // return a month graph
                return Collections.emptyList();
            case 60:
                // return a 2 month graph
                return Collections.emptyList();
            default:
                // return a day graph
        }

        for (long i = length; i >= 0; i -= decrement) {
            history.add(current.minusDays(i).format(DateTimeFormatter.ISO_DATE));
        }


        return history;
    }

    // TODO
    private void setGraphData() {
        return;
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