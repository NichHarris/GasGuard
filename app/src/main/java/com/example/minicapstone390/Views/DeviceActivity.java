package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceActivity extends AppCompatActivity {

    // Declare variables
    private final Database dB = new Database();

    protected SharedPreferenceHelper sharePreferenceHelper;
    protected String deviceId;
    protected Toolbar toolbar;
    protected ListView sensorList;
    protected TextView deviceName, deviceStatus;
    protected List<String> sensorIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        deviceName = (TextView) findViewById(R.id.device_name);
        deviceStatus = (TextView) findViewById(R.id.device_status);

        sensorList = (ListView) findViewById(R.id.sensorList);
        sensorIds = new ArrayList<>();
        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            deviceId = carryOver.getString("deviceId");
            displayDeviceInfo(deviceId);
        } else {
            Toast.makeText(this, "Error fetching device", Toast.LENGTH_LONG).show();
            openHomeActivity();
        }

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
            UpdateDeviceFragment dialog = new UpdateDeviceFragment();
            dialog.show(getSupportFragmentManager(), "Update Device");
        }
        if(id == R.id.disable_device) {
            disableDevice();
        }
        if(id == R.id.remove_device) {
            deleteDevice();
            openHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDeviceStatus(boolean status) {
        dB.getDeviceChild(deviceId).child("status").setValue(status);
    }

    private void disableDevice() {
        dB.getDeviceChild(deviceId).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Boolean.class)) {
                    dB.getDeviceChild(deviceId).child("status").setValue(false);
                } else {
                    dB.getDeviceChild(deviceId).child("status").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });
    }

    private void deleteDevice() {
        // TODO
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void displayDeviceInfo(String deviceId) {
        dB.getDeviceChild(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deviceName.setText(snapshot.child("deviceName").getValue(String.class));
                String status = "Disabled";
                if (snapshot.child("status").getValue(Boolean.class)) {
                    status = "Active";
                }
                deviceStatus.setText(getResources().getString(R.string.status).replace("{0}", status));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });

        dB.getDeviceChild(deviceId).child("sensors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> sensorIds = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    sensorIds.add(ds.getValue(String.class));
                    addToSensorList(ds.getValue(String.class));
                }

                sensorList.setOnItemClickListener((parent, view, position, id) -> {
                    goToSensorActivity(sensorIds.get(position));
                });

                getSensorNames(sensorIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });
    }

    private void getSensorNames(List<String> sensors) {
        List<String> sensorNames = new ArrayList<>();

        for (String id: sensors) {
            DatabaseReference sensorRef = dB.getSensorChild(id);
            sensorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    sensorNames.add(snapshot.child("sensorName").getValue(String.class));
                    setSensorList(sensorNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // TODO: Add error catch
                }
            });
        }
        setSensorList(sensorNames);
    }

    private void setSensorList(List<String> sensors) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensors);
        sensorList.setAdapter(adapter);
    }
    private void addToSensorList(String id) { sensorIds.add(id); }

    private void goToSensorActivity(String sensorId) {
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
