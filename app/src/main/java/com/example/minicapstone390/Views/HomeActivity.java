package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.DeviceAdapter;
import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.R;
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
    protected ProgressBar progressBar;
    protected Toolbar toolbar;

    protected List<String> deviceIds;
    protected ArrayList<Device> devList;

    protected RecyclerView deviceListView;
    protected DeviceAdapter deviceAdapter;

    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;

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
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        deviceIds = new ArrayList<>();
        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);

        devList = new ArrayList<>();

        // Update page info
        updatePage();

        // Recycler View for Devices
        deviceListView = (RecyclerView) findViewById(R.id.devicesRecyclerView);
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(devList);
        deviceListView.setAdapter(deviceAdapter);

        /*
        deviceAdapter.setOnDeviceClickListener(new DeviceAdapter.onDeviceClickListener() {
            @Override
            public void onDeviceClick(int pos) {
                Device device = items.get(pos).getDevice();

                Intent intent = new Intent(getActivity(), Device.class);
                intent.putExtra("clickedDevice", device);
                startActivity(intent);
            }
        */
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> deviceIds = new ArrayList<>();
                // Format List from DB for Adapter
                for (DataSnapshot ds : snapshot.getChildren()) {
                    deviceIds.add(ds.getValue(String.class));
                    addToDeviceList(ds.getValue(String.class));
                }

//                deviceList.setOnItemClickListener((parent, view, position, id) -> {
//                    goToDeviceActivity(deviceIds.get(position));
//                });

                getDeviceNames(deviceIds);
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
        updateProgressBar();
    }

    // TODO
    private void updateProgressBar() {
        // TODO: Get an aggregate of the data from active sensors and devices and display relative health
        progressBar.setProgress(69);
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
    private void getDeviceNames(List<String> devices) {
        ArrayList<Device> devicesData = new ArrayList<>();

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
                        devicesData.add(new Device(devName, devLocation, devStatus));
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        return;
                    }

                    setDeviceList(devicesData);
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
    private void setDeviceList(ArrayList<Device> devicesData) {
        deviceAdapter = new DeviceAdapter(devicesData);
        deviceListView.setAdapter(deviceAdapter);
    }

    // Add device to deviceIds
    public void addToDeviceList(String id) {
        deviceIds.add(id);
    }
}