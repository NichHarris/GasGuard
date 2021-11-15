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
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.DeviceAdapter;
import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected TextView welcomeUserMessage;
    protected BarChart deviceChart;
    protected Toolbar toolbar;

    protected ArrayList<String> deviceIds;
    protected ArrayList<Device> devList;
    protected ArrayList<Device> deviceData;
    protected RecyclerView deviceListView;
    protected DeviceAdapter deviceAdapter;

    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;

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
        // TODO: Bar Chart Used to Display Health Score for Each Device
        deviceChart = (BarChart) findViewById(R.id.deviceChart);
        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);

        // Initialize Dev List and Ids
        devList = new ArrayList<>();
        deviceIds = new ArrayList<>();
        deviceData = new ArrayList<>();
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
        long itemId = item.getItemId();

        if(itemId == R.id.add_device) {
            connectDevice();
        } else if(itemId == R.id.profile) {
            goToProfileActivity();
        } else if(itemId == R.id.device_names) {
            //TODO: Change List of Device Names to Set Names
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
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

    // TODO: android.os.AsyncTask is Deprecated
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

    // Update Page information
    private void updatePage() {
        dB.getUserChild(dB.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String userName = snapshot.child("userName").getValue(String.class);

                    String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", userName != null ? userName : "");
                    welcomeUserMessage.setText(defaultMessage);
                } catch (Exception e) {
                    // Call onCancelled to Throw Exception
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

    // Get List of device names associated with the user
    private void getDeviceNames(List<String> devices) {
        ArrayList<Device> devData = new ArrayList<>();

        for (String id: devices) {
            //TODO: check if devices are part of the user
            DatabaseReference deviceRef = dB.getDeviceRef().child(id);
            deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public String test = "Test";

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

    }

    // Add Devices to ListView from DB Snapshots
    private void setDeviceList(ArrayList<Device> devData) {
        deviceAdapter = new DeviceAdapter(devData);
        deviceListView.setAdapter(deviceAdapter);

        setXAxisLabels(devData);
    }

    // Setting BarChart
    private void setXAxisLabels(ArrayList<Device> deviceData) {
        ArrayList<String> xAxisLabel = new ArrayList<>(deviceData.size());
        for (int i = 0; i < deviceData.size(); i++) {
            if (!xAxisLabel.contains(deviceData.get(i).getDeviceName())) {
                xAxisLabel.add(deviceData.get(i).getDeviceName());
            }
        }

        XAxis xAxis = deviceChart.getXAxis();
        xAxis.setLabelCount(3,true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < xAxisLabel.size()) {
                    return xAxisLabel.get((int) value);
                } else {
                    return "";
                }
            }
        });
        setYAxis();
        setData(deviceData);
    }

    private void setYAxis() {
        YAxis leftAxis = deviceChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1.1f);
        leftAxis.setGranularity(0.1f);

        YAxis rightAxis = deviceChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    protected void setData(ArrayList<Device> devices) {
        deviceChart.clear();
        List<BarEntry> values = new ArrayList<>();
        for (int x = 1; x < devices.size() + 1; x++) {
            long y = 1;
            values.add(new BarEntry(x, y));
        }
        BarDataSet set = new BarDataSet(values, "Test");
        set.setDrawValues(false);
        set.setBarBorderWidth(2f);
        BarData data = new BarData(set);
        data.setValueTextColor(Color.BLACK);
        data.setBarWidth(0.25f);
        data.setValueTextSize(2f);
        deviceChart.setData(data);
        deviceChart.invalidate();
    }

    // Navigation to Profile Activity
    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Navigation to Device Activity
    public void goToDeviceActivity(int index) {
        //Get Device Id from Index
        String deviceId = deviceIds.get(index);

        //Pass Device Id to Get Data for Device Page
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }
}
