package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Models.DatabaseEnv;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.Controllers.DeviceAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final String USERNAME = DatabaseEnv.USERNAME.getEnv();
    private static final String DEVICES = DatabaseEnv.USERDEVICES.getEnv();
    private static final String DEVICENAME = DatabaseEnv.DEVICENAME.getEnv();
    private static final String DEVICELOC = DatabaseEnv.DEVICELOCATION.getEnv();
    private static final String DEVICESTATUS = DatabaseEnv.DEVICESTATUS.getEnv();
    private static final String DEVICECALIBRATION = DatabaseEnv.DEVICECALIBRATION.getEnv();

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
    protected boolean nameState = false; // False = use "deviceName", True = use "deviceId"

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(HomeActivity.this);

        // Set theme
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Layouts
        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);
        deviceChart = (BarChart) findViewById(R.id.deviceChart);
        deviceChart.getLegend().setEnabled(false);
        deviceChart.getDescription().setEnabled(false);

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
        setTheme();
        updatePage();
    }

    // Set theme
    public void setTheme() {
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }
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
            updatePage();
        } else if(itemId == R.id.profile) {
            goToProfileActivity();
        } else if(itemId == R.id.device_names) {
            nameState = !nameState;
            setDropDownText(item);
            loadDeviceList();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Set text change on option selected
    private void setDropDownText(MenuItem item) {
        if(nameState) {
            item.setTitle("Device Names");
        } else {
            item.setTitle("Device IDs");
        }
    }

    // Open connect a device dialog
    public void connectDevice() {
        AddDeviceFragment dialog = new AddDeviceFragment();
        dialog.show(getSupportFragmentManager(), "AddDeviceFragment");
    }

    // Update Page information
    public void updatePage() {
        dB.getUserChild(dB.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child(USERNAME).exists() ? snapshot.child(USERNAME).getValue(String.class) : "";
                    String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", userName != null ? userName : "");
                    welcomeUserMessage.setText(defaultMessage);
                } else {
                    Log.d(TAG, "Unable to get user");
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
        DatabaseReference usersRef = dB.getUserChild(dB.getUserId()).child(DEVICES);

        usersRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Format List from DB for Adapter
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.exists()) {
                        devIds.add(ds.getValue(String.class));
                    } else {
                        Log.e(TAG, "Child does not exist");
                    }
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
        ArrayList<Device> devData = new ArrayList<>();;
        Map<String, Device> deviceMap = new HashMap<String, Device>();

        for (String id: devices) {
            if (id != null) {
                DatabaseReference deviceRef = dB.getDeviceRef().child(id);
                deviceRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Get Device Data from DB
                            String devName = id;
                            if (!nameState) {
                                devName = snapshot.child(DEVICENAME).exists() ? snapshot.child(DEVICENAME).getValue(String.class) : id;
                            }
                            String devLocation = snapshot.child(DEVICELOC).exists() || snapshot.child(DEVICELOC).getValue().equals("") ? snapshot.child(DEVICELOC).getValue(String.class) : "No location set";

                            boolean devCalibration = snapshot.child(DEVICECALIBRATION).exists() ? snapshot.child(DEVICECALIBRATION).getValue(Boolean.class) : true;
                            boolean devStatus = snapshot.child(DEVICESTATUS).exists() ? snapshot.child(DEVICESTATUS).getValue(Boolean.class) : true;

                            // Verify if device is already in the map
                            if (!deviceMap.containsKey(id)) {
                                Device device = new Device(id, devName, devLocation, devStatus, devCalibration);
                                devData.add(device);
                                deviceMap.put(id, device);
                            } else {
                                Device device = deviceMap.get(id);
                                assert device != null;
                                device.setDeviceName(devName);
                                device.setLocation(devLocation);
                                device.setStatus(devStatus);
                                device.setCalibration(devCalibration);
                            }
                            setDeviceList(devData);
                        } else {
                            Log.d(TAG, "Unable to find device");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Log.d(TAG, e.toString());
                        throw e.toException();
                    }
                });
            } else {
                Log.d(TAG, "Id is null");
            }
        }

    }

    // Add Devices to ListView from DB Snapshots
    private void setDeviceList(ArrayList<Device> devData) {
        deviceAdapter = new DeviceAdapter(devData);
        deviceListView.setAdapter(deviceAdapter);
        setXAxis(devData);
    }

    // Format XAxis
    private void setXAxis(ArrayList<Device> deviceData) {
        ArrayList<String> xAxisLabel = new ArrayList<>(deviceData.size());
        for (int i = 0; i < deviceData.size(); i++) {
            if (!xAxisLabel.contains(deviceData.get(i).getDeviceName())) {
                xAxisLabel.add(deviceData.get(i).getDeviceName());
            }
        }

        XAxis xAxis = deviceChart.getXAxis();
        xAxis.setLabelCount(deviceData.size() + 1,true);
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
        setGraphData(deviceData);
    }

    // Format YAxis
    private void setYAxis() {
        YAxis leftAxis = deviceChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setGranularity(10f);

        YAxis rightAxis = deviceChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // Set the devices on the device graph
    protected void setGraphData(ArrayList<Device> devices) {
        deviceChart.clear();
        List<BarEntry> values = new ArrayList<>();
        int i = 0;
        for (int x = 1; x < devices.size() + 1; x++) {
            float y = sharePreferenceHelper.getScore(devices.get(i).getId()) * 100;
            i++;

            values.add(new BarEntry(x, y));
        }
        BarDataSet set = new BarDataSet(values, "DeviceGraph");
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

    // Close app on back pressed
    private void closeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Close app on home menu
    @Override
    public void onBackPressed() {
        closeApp();
    }
}
