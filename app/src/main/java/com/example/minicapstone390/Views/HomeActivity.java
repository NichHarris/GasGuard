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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.DeviceAdapter;
import com.example.minicapstone390.Models.Device;
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

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    protected List<String> deviceIds;
    protected ArrayList<Device> devList;

    protected RecyclerView deviceListView;
    protected DeviceAdapter deviceAdapter;

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

        // Initialize Dev List and Ids
        devList = new ArrayList<>();
        deviceIds = new ArrayList<>();

        // Update page info
        updatePage();

        // Recycler View for Devices
        deviceListView = (RecyclerView) findViewById(R.id.devicesRecyclerView);
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(devList);
        deviceListView.setAdapter(deviceAdapter);
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
                return true;
            case R.id.profile:
                goToProfileActivity();
                return true;
            case R.id.device_names:
                //TODO: change list of device names to set names
                return true;
            default:
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
        ArrayList<String> devIds = new ArrayList<>();

        //Get List of Devices from DB
        DatabaseReference usersRef = dB.getUserChild(dB.getUserId()).child("devices");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Format List from DB for Adapter
                for (DataSnapshot ds : snapshot.getChildren()) {
                    devIds.add(ds.getValue(String.class));
                }

                // Add Ids to Device Ids List
                deviceIds = devIds;

                setXAxisStyle(devIds);
                // Get Device Names from DB given Ids
                getDeviceNames(devIds);
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
                    String userFirstName = snapshot.child("userFirstName").getValue(String.class);

                    String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", userFirstName != null ? userFirstName : "");
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
    public void goToDeviceActivity(int index) {
        System.out.println("All Devices");
        for(String id: deviceIds) {
            System.out.println("Device: " + id);
        }

        String deviceId = deviceIds.get(index);

        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }

    // Get List of device names associated with the user
    private void getDeviceNames(List<String> devices) {
        ArrayList<Device> devData = new ArrayList<>();

        for (String id: devices) {
            //TODO: check if devices are part of the user
            DatabaseReference deviceRef = dB.getDeviceRef().child(id);
            deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        // Get Device Data from DB
                        String devName = snapshot.child("deviceName").getValue(String.class);
                        String devLocation = snapshot.child("location").getValue(String.class);
                        boolean devStatus = snapshot.child("status").getValue(Boolean.class);

                        //Add Device to Device List
                        devData.add(new Device(devName, devLocation, devStatus));
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        return;
                    }

                    setDeviceList(devData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError e) {
                    Log.d(TAG, e.toString());
                    throw e.toException();
                }
            });
        }
        setDeviceList(devData);
    }

    // Add Devices to ListView from DB Snapshots
    private void setDeviceList(ArrayList<Device> devData) {
        deviceAdapter = new DeviceAdapter(devData);
        deviceListView.setAdapter(deviceAdapter);
    }
}
