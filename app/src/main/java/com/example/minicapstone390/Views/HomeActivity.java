package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

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

    protected ListView deviceList;
    protected List<String> deviceIds;
    protected ArrayList<Device> devList;
    protected DeviceAdapter deviceAdapter;
    protected RecyclerView rc;

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
        deviceList = (ListView) findViewById(R.id.deviceDataList);

        devList = new ArrayList<>();

        // Update page info
        // TODO: Commented for testing, uncomment now
        updatePage();

        // Device Names
//        ArrayList<Device> dnames = new ArrayList<>();
//        dnames.add(new Device("nice", "Downtown Montreal, QC", false));
//        dnames.add(new Device("many devices", "DDO, QC", true));
//        dnames.add(new Device("cool", "Vaudreil, QC", true));

        // Recycler View for Devices
        rc = (RecyclerView) findViewById(R.id.devicesRecyclerView);
        deviceAdapter = new DeviceAdapter(devList);
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(deviceAdapter);

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
        // TODO: Commented for testing, uncomment now
        //updatePage();
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

                deviceList.setOnItemClickListener((parent, view, position, id) -> {
                    goToDeviceActivity(deviceIds.get(position));
                });

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
                    String userName = snapshot.child("username").getValue(String.class);
                    String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", userName != null ? userName : "");
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
        // TODO: Commented for testing, uncomment now
        //loadDeviceList();
        updateProgressBar();
    }

    // TODO
    private void updateProgressBar() {
        // TODO: Get an aggregate of the data from active sensors and devices and display relative health
        progressBar.setProgress(66);
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