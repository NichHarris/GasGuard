package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;

import com.example.minicapstone390.Models.Sensor;
import com.example.minicapstone390.R;
import com.example.minicapstone390.Controllers.SensorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeviceActivity extends AppCompatActivity {
    private static final String TAG = "DeviceActivity";

    // Declare variables
    private final Database dB = new Database();

    protected SharedPreferenceHelper sharePreferenceHelper;
    protected String deviceId;
    protected String function;
    protected Toolbar toolbar;
    protected TextView deviceName, deviceStatus;

    protected List<String> sensorIds = new ArrayList<>();
    protected ArrayList<Sensor> sensorList;

    protected RecyclerView sensorListView;
    protected SensorAdapter sensorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(DeviceActivity.this);

        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sensorListView = (RecyclerView) findViewById(R.id.sensorsRecyclerView);
        sensorListView.setLayoutManager(new LinearLayoutManager(this));
        sensorAdapter = new SensorAdapter(sensorList);
        sensorListView.setAdapter(sensorAdapter);

        // Initialize TextViews
        deviceName = (TextView) findViewById(R.id.device_name);
        deviceStatus = (TextView) findViewById(R.id.device_status);

        // Initialize Dev List and Ids
        sensorList = new ArrayList<>();
        sensorIds = new ArrayList<>();

        // Display info for selected device
        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            deviceId = carryOver.getString("deviceId");
            function = carryOver.getString("editDevice", "");
            if (function.equals("editDevice()")) {
                editDevice(deviceId);
            }
            if (deviceId != null) {
                displayDeviceInfo(deviceId);
            } else {
                Log.e(TAG, "Id is null");
                openHomeActivity();
            }
        } else {
            Toast.makeText(this, "Error fetching device", Toast.LENGTH_LONG).show();
            Log.d(TAG, "No deviceId carry over, returning to HomeActivity");
            openHomeActivity();
        }
    }

    private void editDevice(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("id", deviceId);
        UpdateDeviceFragment dialog = new UpdateDeviceFragment();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "UpdateDeviceFragment");
    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.update_device) {
            editDevice(deviceId);
        } else if(id == R.id.disable_device) {
            disableDevice();
        } else if(id == R.id.remove_device) {
            deleteDevice();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Change the active status of a device
    private void disableDevice() {
        dB.getDeviceChild(deviceId).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean status = true;

                try {
                    if (snapshot.getValue(Boolean.class)) {
                        status = false;
                    }
                    dB.getDeviceChild(deviceId).child("status").setValue(status);
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

    // Remove device from the user
    private void deleteDevice() {
        // TODO: Copied from android jdk just modify it
        // TODO: Create a builder class...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Device Confirmation");
        builder.setMessage("Deleting will completely remove the device from user and its stored data");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dB.getUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    openHomeActivity();
                                } else {
                                    // TODO: Send toast for failed delete
                                }
                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO LOG THAT IT IS A CANCEL
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Navigate to the HomeActivity
    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Display relevant device information
    private void displayDeviceInfo(String deviceId) {
        dB.getDeviceChild(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String devNameText = "Device: " + snapshot.child("deviceName").getValue(String.class);
                deviceName.setText(devNameText);

                String status = getResources().getString(R.string.inactiveDeviceStatus);
                try {
                    if (snapshot.child("status").getValue(Boolean.class)) {
                        status = getResources().getString(R.string.activeDeviceStatus);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    throw e;
                }

                String devStatusText = getResources().getString(R.string.status) +  status;
                deviceStatus.setText(devStatusText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });

        dB.getDeviceChild(deviceId).child("sensors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    sensorIds.add(ds.getValue(String.class));
                }

                getSensorNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
            }
        });
    }

    // Get List of all sensor names
    private void getSensorNames() {
        ArrayList<Sensor> sensData = new ArrayList<>();
        Map<String, Sensor> sensorMap = new HashMap<String, Sensor>();
        for (String id: sensorIds) {
            DatabaseReference sensorRef = dB.getSensorChild(id);
            sensorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String sensorName = snapshot.child("SensorName").getValue(String.class);
                        int sensorType =  snapshot.child("SensorType").getValue(Integer.class) != null ? snapshot.child("SensorType").getValue(Integer.class): 0;
                        double sensorValue = snapshot.child("SensorValue").getValue(Double.class);
                        if (!sensorMap.containsKey(id)) {
                            Sensor sensor = new Sensor(id, sensorType, sensorName, sensorValue);
                            sensData.add(sensor);
                            sensorMap.put(id, sensor);
                        } else {
                            Sensor sensor = sensorMap.get(id);
                            assert sensor != null;
                            sensor.setSensorName(sensorName);
                            sensor.setSensorValue(sensorValue);
                            sensor.setSensorType(sensorType);
                            sensData.set(sensData.indexOf(sensor), sensor);
                        }
                        setSensorList(sensData);
                    } catch (Exception e) {
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


    }

    // Set ListView of sensors
    private void setSensorList(ArrayList<Sensor> sensData) {
        // TODO: Limit Sensor Name to 13 Characters
        // Recycler View for Sensors
        sensorAdapter = new SensorAdapter(sensData);
        sensorListView.setAdapter(sensorAdapter);
    }

    // Open sensor activity for selected sensor
    public void goToSensorActivity(int sensorIndex) {
        String sensorId = sensorIds.get(sensorIndex);

        Intent intent = new Intent(this, SensorActivity.class);
        intent.putExtra("sensorId", sensorId);
        startActivity(intent);
    }

    // Navigate back to homepage on task-bar return
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
