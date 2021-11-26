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
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;

import com.example.minicapstone390.Controllers.Threshold;
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
    private static final String DEVICES = DatabaseEnv.USERDEVICES.getEnv();
    private static final String DEVICENAME = DatabaseEnv.DEVICENAME.getEnv();
    private static final String DEVICESTATUS = DatabaseEnv.DEVICESTATUS.getEnv();
    private static final String DEVICESENSORS = DatabaseEnv.DEVICESENSORS.getEnv();
    private static final String SENSORNAME = DatabaseEnv.SENSORNAME.getEnv();
    private static final String SENSORTYPE = DatabaseEnv.SENSORTYPE.getEnv();
    private static final String SENSORVALUE = DatabaseEnv.SENSORVALUE.getEnv();
    private static final String SENSORSCORE = DatabaseEnv.SENSORSCORE.getEnv();

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
            function = carryOver.getString("callFunction", "");
            if (function.equals("editDevice()")) {
                editDevice(deviceId);
            } else if (function.equals("deleteDevice()")) {
                deleteDevice(deviceId);
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
        } else if(id == R.id.calibrate_device) {
            calibrateDevice(item);
        } else if(id == R.id.disable_device) {
            disableDevice(item);
        } else if(id == R.id.remove_device) {
            deleteDevice(deviceId);
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Set text change on option selected
    public void setDropDownText(MenuItem item, boolean status) {
        if(!status) {
            item.setTitle("Enable Device");
        } else {
            item.setTitle("Disable Device");
        }
    }

    public void calibrateDevice(MenuItem item) {
        // TODO: Copied from android jdk just modify it
        // TODO: Create a builder class...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Calibrate Device Confirmation");
        builder.setMessage("Calibrating will completely remove all stored data from device");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dB.getDeviceChild(deviceId).child("CalibrationStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    dB.getDeviceChild(deviceId).child("CalibrationStatus").setValue(false);
                                    Toast.makeText(DeviceActivity.this, "Calibration started, please leave device for 30 min", Toast.LENGTH_LONG).show();

                                    // TODO: Clear all sensor values
                                } else {
                                    Log.e(TAG, "Unable to get calibration status");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError e) {
                                Log.d(TAG, e.toString());
                                throw e.toException();
                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Device calibration cancelled");
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Change the active status of a device
    public void disableDevice(MenuItem item) {
        dB.getDeviceChild(deviceId).child(DEVICESTATUS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean status = true;

                try {
                    if (snapshot.exists()) {
                        if (snapshot.getValue(Boolean.class)) {
                            status = false;
                        }

                        dB.getDeviceChild(deviceId).child(DEVICESTATUS).setValue(status);
                        setDropDownText(item, status);
                    } else {
                        Log.e(TAG, "Unable to get status");
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

    // Remove device from the user
    public void deleteDevice(String deviceId) {
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
                        if(dB.getUserChild().child(DEVICES) != null){
                            dB.getUserChild().child(DEVICES).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        if (ds.getValue(String.class).equals(deviceId)) {
                                            ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.d(TAG, String.format("Unable to remove device: %s", deviceId));
                                                    } else {
                                                        Log.i(TAG, String.format("Removed device: %s", deviceId));
                                                        openHomeActivity();
                                                    }
                                                }
                                            });
                                        }
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
                        Log.i(TAG, "Device delete cancelled");
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
    public void displayDeviceInfo(String deviceId) {
        dB.getDeviceChild(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String devNameText = snapshot.child(DEVICENAME).exists() ? snapshot.child(DEVICENAME).getValue(String.class) : "0";
                deviceName.setText("Device: " + devNameText);

                String status = getResources().getString(R.string.inactiveDeviceStatus);
                try {
                    if (snapshot.child(DEVICESTATUS).exists()) {
                        if (snapshot.child(DEVICESTATUS).getValue(Boolean.class)) {
                            status = getResources().getString(R.string.activeDeviceStatus);
                        }
                    } else {
                        Log.e(TAG, "Unable to locate device status");
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

        dB.getDeviceChild(deviceId).child(DEVICESENSORS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.exists()) {
                        sensorIds.add(ds.getValue(String.class));
                    } else {
                        Log.e(TAG, "Unable to locate sensor");
                    }
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
    public void getSensorNames() {
        ArrayList<Sensor> sensData = new ArrayList<>();
        Map<String, Sensor> sensorMap = new HashMap<String, Sensor>();
        for (String id: sensorIds) {
            DatabaseReference sensorRef = dB.getSensorChild(id);
            sensorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String sensorName = snapshot.child(SENSORNAME).exists() ? snapshot.child(SENSORNAME).getValue(String.class) : id;
                        int sensorType =  snapshot.child(SENSORTYPE).exists() ? snapshot.child(SENSORTYPE).getValue(Integer.class): 0;
                        double sensorValue = snapshot.child(SENSORVALUE).exists() ? snapshot.child(SENSORVALUE).getValue(Double.class): 0.0;
                        boolean status = true;
                        double sensorScore = snapshot.child(SENSORSCORE).exists() ? snapshot.child(SENSORSCORE).getValue(Double.class) : 0.0;

                        if (sensorScore >= sensorThreshold(sensorType) && sensorThreshold(sensorType) != 0.0) {
                            status = false;
                            Log.i(TAG,  String.format("Sensor Threshold reached: %d", sensorType));
                            // TODO call a notification
                        }

                        // TODO: Function to determine if the sensor threshold is exceeded;
                        if (!sensorMap.containsKey(id)) {
                            Sensor sensor = new Sensor(id, sensorType, sensorName, sensorValue, status, sensorScore);
                            sensData.add(sensor);
                            sensorMap.put(id, sensor);
                        } else {
                            Sensor sensor = sensorMap.get(id);
                            assert sensor != null;
                            sensor.setSensorName(sensorName);
                            sensor.setSensorValue(sensorValue);
                            sensor.setSensorType(sensorType);
                            sensor.setSensorScore(sensorScore);
                            sensor.setStatus(status);
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

    public double sensorThreshold(int type) {
        String strType = "MQ" + type;
        double threshold = 0.0;
        switch (strType) {
            case "MQ2":
                threshold = Threshold.MQ2.getThreshold();
                break;
            case "MQ3":
                threshold = Threshold.MQ3.getThreshold();
                break;
            case "MQ4":
                threshold = Threshold.MQ4.getThreshold();
                break;
            case "MQ6":
                threshold = Threshold.MQ6.getThreshold();
                break;
            case "MQ7":
                threshold = Threshold.MQ7.getThreshold();
                break;
            case "MQ8":
                threshold = Threshold.MQ8.getThreshold();
                break;
            case "MQ9":
                threshold = Threshold.MQ9.getThreshold();
                break;
            case "MQ135":
                threshold = Threshold.MQ135.getThreshold();
                break;
        }
        return threshold;
    }

    // Set ListView of sensors
    public void setSensorList(ArrayList<Sensor> sensData) {
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

    // Open sensor activity for selected sensor
    public void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Navigate back to homepage on task-bar return
    @Override
    public boolean onSupportNavigateUp() {
        goToHomeActivity();
        return true;
    }
}
