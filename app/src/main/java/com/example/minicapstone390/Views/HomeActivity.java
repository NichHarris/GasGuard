package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ListView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected TextView welcomeUserMessage;
    protected BarChart deviceChart;
    protected Toolbar toolbar;
    protected ListView deviceList;
    protected List<String> deviceIds;

    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public ArrayList<String> test;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(HomeActivity.this);
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Layouts
        // TODO: Replace progress bar with BarGraph of each device
        deviceChart = (BarChart) findViewById(R.id.deviceChart);
        deviceIds = new ArrayList<>();
        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);
        deviceList = (ListView) findViewById(R.id.deviceDataList);

        // Update page info
        updatePage();
//        setXAxisStyle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePage();
    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_device:
                connectDevice();
                break;
            case R.id.profile:
                goToProfileActivity();
                break;
            case R.id.device_names:
                //TODO: change list of device names to set names
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Fix spaghetti
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setXAxisStyle(ArrayList<String> test) {
        XAxis xAxis = deviceChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(0, 0, 0));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return test.get((int) value);
            }
        });

        setYAxisStyle();
//        System.out.println("Result: " + producer());
        setData(test);
    }

    protected void setYAxisStyle() {
        YAxis leftAxis = deviceChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1.1f);
        leftAxis.setGranularity(0.1f);
        leftAxis.setYOffset(0f);
        leftAxis.setTextColor(Color.rgb(0, 0, 0));

        YAxis rightAxis = deviceChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // TODO: Fix spaghetti
    protected void setData(ArrayList<String> test) {
        List<BarEntry> values = new ArrayList<>();

        for (int x = 1; x < test.size(); x++) {
            long y = x + 1;
            values.add(new BarEntry(x, y));
        }
        BarDataSet set = new BarDataSet(values, "Test");
        set.setDrawValues(false);
        set.setBarBorderWidth(2f);

        BarData data = new BarData(set);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        deviceChart.setData(data);
        deviceChart.invalidate();
    }

    // TODO: IMPLEMENT DEVICE CONNECTION
    public void connectDevice() {
        getIpAndPort();
        Socket_AsyncTask connect_device = new Socket_AsyncTask();
        connect_device.execute();
    }

    // TODO
    public void getIpAndPort() {
        DeviceFragment dialog = new DeviceFragment();
        dialog.show(getSupportFragmentManager(), "AddDeviceFragment");
    }

    // TODO
    public static class Socket_AsyncTask extends AsyncTask<Void, Void, Void> {
        Socket socket;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                InetAddress inetAddress = InetAddress.getByName(HomeActivity.wifiModuleIp);
                socket = new java.net.Socket(inetAddress, HomeActivity.wifiModulePort);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // Get, Initialize, and Update Devices - Display List of Devices
    protected void loadDeviceList() {
        //Get List of Devices from DB
        DatabaseReference usersRef = dB.getUserChild(dB.getUserId()).child("devices");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> deviceIds = new ArrayList<>();
                // Format List from DB for Adapter
                for (DataSnapshot ds : snapshot.getChildren()) {
                    deviceIds.add(ds.getValue(String.class));
                    addToDeviceList(ds.getValue(String.class));
                }

                deviceList.setOnItemClickListener((parent, view, position, id) -> {
                    goToDeviceActivity(deviceIds.get(position));
                });

                test = getDeviceNames(deviceIds);
                setXAxisStyle(test);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
    }

    // Update Page information
    private void updatePage() {
        dB.getUserChild(dB.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", snapshot.child("userName").getValue(String.class));
                    welcomeUserMessage.setText(defaultMessage);
                } catch (Exception e) {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
        loadDeviceList();
    }

    // Navigation to Profile Activity
    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Navigation to Device Activity
    private void goToDeviceActivity(String deviceId) {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }

    // Get List of device names associated with the user
    private ArrayList<String> getDeviceNames(List<String> devices) {
        List<String> deviceNames = new ArrayList<>();

        for (String id: devices) {
            //TODO: check if devices are part of the user
            DatabaseReference deviceRef = dB.getDeviceRef().child(id);
            deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        deviceNames.add(snapshot.child("deviceName").getValue(String.class));
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        return;
                    }
                    setDeviceList(deviceNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError e) {
                    Log.d(TAG, e.toString());
                    throw e.toException();
                }
            });
        }
        setDeviceList(deviceNames);
        return new ArrayList<>(deviceNames);
    }

    // Add Devices to ListView
    private void setDeviceList(List<String> devices) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        deviceList.setAdapter(adapter);
    }

    // Add device to deviceIds
    public void addToDeviceList(String id) {
        deviceIds.add(id);
    }
}