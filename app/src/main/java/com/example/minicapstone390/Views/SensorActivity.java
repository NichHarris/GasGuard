package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.content.DialogInterface;
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
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.Models.SensorData;
import com.example.minicapstone390.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

// DateTime
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

public class SensorActivity extends AppCompatActivity {
    private static final String TAG = "SensorActivity";
    private static final String SENSORPAST = DatabaseEnv.SENSORPAST.getEnv();
    private static final String SENSORNAME = DatabaseEnv.SENSORNAME.getEnv();
    private static final String VALUE = DatabaseEnv.VALUE.getEnv();
    private static final String SENSORSTATUS = DatabaseEnv.SENSORSTATUS.getEnv();
    private static final String SENSORSCORE = DatabaseEnv.SENSORSCORE.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected LineChart sensorChart;
    protected TextView chartTitle;
    protected RadioGroup graphTimesOptions;
    protected Toolbar toolbar;
    protected String sensorId;
    protected String function;
    protected double score = 0.0;
    public int graphTimeScale = 0;
    public long delta = 30;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(SensorActivity.this);
        // Set theme
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        graphTimesOptions = (RadioGroup) findViewById(R.id.graphTimeOptions);
        graphTimesOptions.check(R.id.dayButton);

        chartTitle = (TextView) findViewById(R.id.chart_title);
        sensorChart = (LineChart) findViewById(R.id.sensorChart);
        // Disable legend and description
        sensorChart.getLegend().setEnabled(false);
        sensorChart.getDescription().setEnabled(false);

        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            sensorId = carryOver.getString("sensorId");
            function = carryOver.getString("callFunction", "");

            if(function.equals("editSensor()")) {
                editSensor(sensorId);
            } else if (function.equals("deleteSensor()")) {
                deleteSensorData(sensorId);
            }
            if (sensorId != null) {
                displaySensorInfo(sensorId);
                getAllSensorData();
            } else {
                Log.e(TAG, "Id is null");
                openHomeActivity();
            }
        } else {
            Toast.makeText(this, "Error fetching device", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error fetching device");
            openHomeActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        setTheme();
        displaySensorInfo(sensorId);
        setGraphScale();
    }

    public void setTheme() {
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }
    }

    public void deleteSensorData(String sensorId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Sensor Data Confirmation");
        builder.setMessage("Deleting will completely remove the Sensors stored data");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dB.getSensorChild(sensorId) != null) {
                            dB.getSensorChild(sensorId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child(SENSORPAST).exists()) {
                                        snapshot.child(SENSORPAST).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.d(TAG, String.format("Unable to delete sensor data: %s", sensorId));
                                                } else {
                                                    Log.i(TAG, String.format("Removed sensor data: %s", sensorId));
                                                    onSupportNavigateUp();
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e(TAG, "Error retrieving SensorPastValues from DB");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError e) {
                                    Log.d(TAG, e.toString());
                                    throw e.toException();
                                }
                            });
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Sensor data delete cancelled");
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editSensor(String sensorId) {
        Bundle bundle = new Bundle();
        bundle.putString("id", sensorId);
        SensorFragment dialog = new SensorFragment();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SensorFragment");
    }

    // Display basic info of the sensor
    public void displaySensorInfo(String sensorId) {
        DatabaseReference sensorRef = dB.getSensorChild(sensorId);

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chartTitle.setText(getResources().getString(R.string.sensor_graph).replace("{0}", Objects.requireNonNull(snapshot.child(SENSORNAME).getValue(String.class))));
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
    public void setGraphScale() {
        graphTimesOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.weekButton:
                        graphTimeScale = 7;
                        delta = 90;
                        break;
                    case R.id.weeksButton:
                        graphTimeScale = 14;
                        delta = 180;
                        break;
                    case R.id.monthButton:
                        graphTimeScale = 28;
                        delta = 360;
                        break;
                    default:
                        graphTimeScale = 0;
                        delta = 30;
                }
                getAllSensorData();
            }
        });
    }

    // TODO: Fix spaghetti
    // Get the time scale of the X axis of the graph
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LocalDateTime> updateGraphDates() {
        List<LocalDateTime> history = new ArrayList<>();
        setGraphScale();
        long decrement = graphTimeScale / 7;
        if (decrement == 0) {
            for (long i = 23; i >= 0; i -= 4) {
                history.add(LocalDateTime.now().minusHours(i));
            }
            history.add(LocalDateTime.now());
        } else {
            for (long i = graphTimeScale; i >= 0; i -= decrement) {
                history.add(LocalDateTime.now().minusDays(i));
            }
        }
        return new ArrayList<>(history);
    }

    public void getAllSensorData() {
        ArrayList<SensorData> validData = new ArrayList<>();
        dB.getSensorChild(sensorId).child(SENSORPAST).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Double> values = new ArrayList<>();
                ArrayList<LocalDateTime> times = new ArrayList<>();
                ArrayList<LocalDateTime> history = updateGraphDates();

                LocalDateTime start = history.get(0);
                LocalDateTime end = history.get(history.size() - 1);

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.exists()) {
                        LocalDateTime time = LocalDateTime.parse(ds.getKey(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        if (start.isBefore(time) && end.isAfter(time)) {
                            if (ds.child(VALUE).exists()) {
                                values.add(ds.child(VALUE).getValue(Double.class));
                                times.add(LocalDateTime.parse(ds.getKey(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            } else {
                                Log.e(TAG, "Error retrieving PastValues Value from DB");
                            }
                        }
                    } else {
                        Log.e(TAG, "Error retrieving SensorPastValues from DB");
                    }
                }
                validData.add(new SensorData(values, times));
                calculateThreshold(validData);
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

    // TODO May need to move logic around so stuff is always calculated...
    public void calculateThreshold(ArrayList<SensorData> data) {
        if (data.size() != 0) {
            if (data.get(0).getValues().size() != 0) {
                int start = 0;
                int size = data.get(0).getValues().size();
                if (size > 10) {
                    start = data.get(0).getValues().size() - 1 - 10;
                    size = 10;
                }

                double sum = 0;
                for (int i = start; i < data.get(0).getValues().size(); i++) {
                    sum += data.get(0).getValues().get(i);
                }
                
                score = sum / (size + 1);
                //TODO Convert to PPM value to display on DeviceActivity and home screen
                dB.getSensorChild(sensorId).child(SENSORSCORE).setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Unable to access SensorScore");
                        }
                    }
                });
            } else {
                Log.d(TAG, "Data values size is 0");
            }
        } else {            
            Log.d(TAG, "Data size is 0");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void producer(List<LocalDateTime> history, SensorData data) {
        if (data.getValues().size() != 0) {
            LocalDateTime start = history.get(0);
            LocalDateTime end = history.get(history.size() - 1);
            long duration = Duration.between(start, end).getSeconds();
//            long delta = 60;
            long size = duration / delta;
//            long size = data.getValues().size() != 0 ? data.getValues().size() : 1;
//            long delta = duration/size;
            ArrayList<LocalDateTime> results = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                results.add(start.plusSeconds(i * delta));
            }
            setXAxisLabels(history, data, results);
        }
    }

    // Setting LineChart
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setXAxisLabels(List<LocalDateTime> history, SensorData data, ArrayList<LocalDateTime> results) {
        if (results.size() != 0) {
            ArrayList<String> xAxisLabel = new ArrayList<>(results.size());
            DateTimeFormatter format;
            if (graphTimesOptions.getCheckedRadioButtonId() == R.id.dayButton) {
                format = DateTimeFormatter.ofPattern("HH:mm");
            } else {
                format = DateTimeFormatter.ofPattern("MM/dd");
            }

            for (int i = 0; i < results.size(); i++) {
                xAxisLabel.add(results.get(i).format(format));
            }

            XAxis xAxis = sensorChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelCount(history.size() + 1, true);
            xAxis.setCenterAxisLabels(true);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < results.size()) {
                        return xAxisLabel.get((int) value);
                    }
                    return "";
                }
            });
            setYAxis();
            setData(data, results);
        } else {
            Log.d(TAG, "Result is empty");
        }
    }

    public void setYAxis() {
        YAxis leftAxis = sensorChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(300f);
        leftAxis.setGranularity(30f);

        YAxis rightAxis = sensorChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // TODO: Fix spaghetti
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setData(SensorData data, ArrayList<LocalDateTime> results) {
        
        if (data.getValues().size() != 0) {
            ArrayList<Entry> values = new ArrayList<>();
            int i = 0;
            for (int x = 1; x < results.size() - 1; x++) {
                LocalDateTime start = data.getTimes().get(0);
                LocalDateTime end = data.getTimes().get(data.getTimes().size() - 1);
                // TODO: Check if first state is ever passing? Appending -1 to start isn't working
                if (results.get(x).isBefore(start) || results.get(x).isAfter(end) || i >= data.getValues().size()) {
                    values.add(new Entry(x, -1));
                } else {
                    values.add(new Entry(x, data.getValues().get(i).floatValue()));
                    i++;
                }
            }

            if (values.size() != 0) {
                LineDataSet set = new LineDataSet(values, "SensorGraph");
                set.setDrawValues(false);
                set.setLineWidth(2);

                LineData lineData = new LineData(set);
                lineData.setValueTextColor(Color.BLACK);
                lineData.setValueTextSize(9f);
//        sensorChart.setBackgroundColor(Color.WHITE);
                sensorChart.setData(lineData);
                sensorChart.invalidate();
            } else {
                Log.d(TAG, "Value is empty");
            }
        } else {
            Log.d(TAG, "Data is empty");
        }
    }

    public void notification() {
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
            Log.d(TAG, "Disable sensor called but not implemented");
//            disableSensor();
        } else if (id == R.id.delete_sensor_data) {
            deleteSensorData(sensorId);
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // TODO: Add status to DB
    public void disableSensor() {
        dB.getSensorChild(sensorId).child(SENSORSTATUS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        if (snapshot.getValue(Boolean.class)) {
                            snapshot.getRef().setValue(false);
                        } else {
                            snapshot.getRef().setValue(true);
                        }
                    } else {
                        Log.e(TAG, "Error retrieving sensor status from DB");
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
    public void openHomeActivity() {
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
