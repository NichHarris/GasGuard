package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.minicapstone390.Models.DatabaseEnv;
import com.example.minicapstone390.Models.GasType;
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
    private static final String SENSORTYPE = DatabaseEnv.SENSORTYPE.getEnv();
    private static final String SENSORSTATUS = DatabaseEnv.SENSORSTATUS.getEnv();
    private static final String SENSORSCORE = DatabaseEnv.SENSORSCORE.getEnv();
    private static final String VALUE = DatabaseEnv.VALUE.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected LineChart sensorChart;
    protected TextView chartTitle, sensorName, sensorStatus, sensorType, sensorGas;
    protected RadioGroup graphTimesOptions;
    protected Toolbar toolbar;
    protected String deviceId, sensorId, function;

    // Default Values
    protected double score = 0.0;
    protected int graphTimeScale = 0;

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

        // Sensor Info
        sensorName = (TextView) findViewById(R.id.sensor_name);
        sensorStatus = (TextView) findViewById(R.id.sensor_status);
        sensorType = (TextView) findViewById(R.id.sensor_type);
        sensorGas = (TextView) findViewById(R.id.sensor_gas);

        // Chart Info
        chartTitle = (TextView) findViewById(R.id.chart_title);
        sensorChart = (LineChart) findViewById(R.id.sensorChart);
        sensorChart.getLegend().setEnabled(false);
        sensorChart.getDescription().setEnabled(false);

        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            sensorId = carryOver.getString("sensorId");
            function = carryOver.getString("callFunction", "");
            deviceId = carryOver.getString("deviceId");

            // Check if function was called outside of activity
            if(function.equals("editSensor()")) {
                editSensor(sensorId);
            } else if (function.equals("deleteSensor()")) {
                deleteSensorData(sensorId);
            }

            // Verify id exists
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

    // Set theme
    public void setTheme() {
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }
    }

    // Remove all sensor past data
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

    // Open edit sensor dialog
    public void editSensor(String sensorId) {
        Bundle bundle = new Bundle();
        bundle.putString("id", sensorId);
        SensorFragment dialog = new SensorFragment();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SensorFragment");
    }

    // Display basic info of the sensor
    public void displaySensorInfo(String sensorId) {
        dB.getSensorChild(sensorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child(SENSORNAME).exists()) {
                    sensorName.setText(getResources().getString(R.string.sensor_name) + " " + Objects.requireNonNull(snapshot.child(SENSORNAME).getValue(String.class)));

                    String sensorStatusText = getResources().getString(R.string.unsafeSensorValue);
                    if(snapshot.child(SENSORSTATUS).exists() && snapshot.child(SENSORSTATUS).getValue(Boolean.class))
                        sensorStatusText = getResources().getString(R.string.safeSensorValue);
                    sensorStatus.setText(getResources().getString(R.string.status) + " " + sensorStatusText);

                    int sensorTypeValue = snapshot.child(SENSORTYPE).exists() ? snapshot.child(SENSORTYPE).getValue(Integer.class) : 1;
                    sensorType.setText(getResources().getString(R.string.sensor_type) + " MQ" + sensorTypeValue);
                    sensorGas.setText(getResources().getString(R.string.sensor_gas) + " " + getGasType(sensorTypeValue));

                    chartTitle.setText(getResources().getString(R.string.sensor_graph).replace("{0}", Objects.requireNonNull(snapshot.child(SENSORNAME).getValue(String.class))));
                } else {
                    Log.d(TAG, "Unable to find sensor");
                }
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
                        break;
                    case R.id.weeksButton:
                        graphTimeScale = 14;
                        break;
                    case R.id.monthButton:
                        graphTimeScale = 28;
                        break;
                    default:
                        graphTimeScale = 0;
                }
                getAllSensorData();
            }
        });
    }

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

    // Get the past values for the current sensor
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
                        // Only add those within the selected time range
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
                formatData(history, validData.get(0));
                validData.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
    }

    // Calculate if threshold has been exceeded
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

    // Format number of entries to XAxis
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void formatData(List<LocalDateTime> history, SensorData data) {
        if (data.getValues().size() != 0) {
            LocalDateTime start = history.get(0);
            LocalDateTime end = history.get(history.size() - 1);
            long offset = Duration.between(start, end).getSeconds() / data.getValues().size();
            ArrayList<LocalDateTime> xAxisTimes = new ArrayList<>();
            // Create X axis entries
            for (int i = 0; i < data.getValues().size(); i++) {
                xAxisTimes.add(start.plusSeconds(i * offset));
            }
            setXAxis(history, data, xAxisTimes);
        }
    }

    // Format XAxis
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setXAxis(List<LocalDateTime> history, SensorData data, ArrayList<LocalDateTime> xAxisTimes) {
        if (xAxisTimes.size() != 0) {
            ArrayList<String> xAxisLabel = new ArrayList<>(xAxisTimes.size());
            DateTimeFormatter format;

            // Format by time or by date
            if (graphTimesOptions.getCheckedRadioButtonId() == R.id.dayButton) {
                format = DateTimeFormatter.ofPattern("HH:mm");
            } else {
                format = DateTimeFormatter.ofPattern("MM/dd");
            }

            for (int i = 0; i < xAxisTimes.size(); i++) {
                xAxisLabel.add(xAxisTimes.get(i).format(format));
            }

            XAxis xAxis = sensorChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelCount(history.size() + 1, true);
            xAxis.setCenterAxisLabels(true);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < xAxisLabel.size()) {
                        return xAxisLabel.get((int) value);
                    }
                    return "";
                }
            });
            setYAxis();
            setGraphData(data, xAxisTimes);
        } else {
            Log.d(TAG, "Result is empty");
        }
    }

    // Format YAxis
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

    // Set the formatted data on the sensor graph
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setGraphData(SensorData data, ArrayList<LocalDateTime> xAxisTimes) {

        // Verify if data exists
        if (data.getValues().size() != 0) {
            ArrayList<Entry> values = new ArrayList<>();
            int i = 0;
            for (int x = 1; x < xAxisTimes.size() - 1; x++) {
                // Get the start time and end time of the first and last registered data
                LocalDateTime start = data.getTimes().get(0);
                LocalDateTime end = data.getTimes().get(data.getTimes().size() - 1);

                // If the data is out the time range append -1 else add to graph
                if (xAxisTimes.get(x).isBefore(start) || xAxisTimes.get(x).isAfter(end) || i >= data.getValues().size()) {
                    values.add(new Entry(x, -1));
                } else {
                    values.add(new Entry(x, data.getValues().get(i).floatValue()));
                    i++;
                }
            }

            // Verify data was added
            if (values.size() != 0) {
                LineDataSet set = new LineDataSet(values, "SensorGraph");
                set.setDrawValues(false);
                set.setLineWidth(2);

                LineData lineData = new LineData(set);
                lineData.setValueTextColor(Color.BLACK);
                lineData.setValueTextSize(9f);
                sensorChart.setData(lineData);
                sensorChart.invalidate();
            } else {
                Log.d(TAG, "Value is empty");
            }
        } else {
            Log.d(TAG, "Data is empty");
        }
    }

    // Return the type of gas the sensor measures
    public String getGasType(int type) {
        String strType = "MQ" + type;
        switch (strType) {
            case "MQ2":
                return GasType.MQ2GAS.getGasType();
            case "MQ3":
                return GasType.MQ3GAS.getGasType();
            case "MQ4":
                return GasType.MQ4GAS.getGasType();
            case "MQ6":
                return GasType.MQ6GAS.getGasType();
            case "MQ7":
                return GasType.MQ7GAS.getGasType();
            case "MQ8":
                return GasType.MQ8GAS.getGasType();
            case "MQ9":
                return GasType.MQ9GAS.getGasType();
            case "MQ135":
                return GasType.MQ135GAS.getGasType();
            default:
                return "No Gas";
        }
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
        if (id == R.id.delete_sensor_data) {
            deleteSensorData(sensorId);
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Navigation to Device Activity
    public void goToDeviceActivity() {
        if (deviceId == null) {
            openHomeActivity();
        } else {
            //Pass Device Id to Get Data for Device Page
            Intent intent = new Intent(this, DeviceActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        }
    }

    // Navigate back to Home Activity
    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Navigate back to device page on task-bar return
    @Override
    public boolean onSupportNavigateUp() {
        goToDeviceActivity();
        return true;
    }
}
