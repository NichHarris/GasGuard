package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
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

public class DeviceActivity extends AppCompatActivity {

    // Initialize variables
    private final Database dB = new Database();

    protected String deviceId;
    protected Toolbar toolbar;
    protected ListView sensorList;
    protected TextView deviceName; //TODO
    protected List<String> sensorIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

//        // Add task-bar
//        assert getSupportActionBar() != null;
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceName = (TextView) findViewById(R.id.device_name);

        sensorList = (ListView) findViewById(R.id.sensorList);
        sensorIds = new ArrayList<>();
        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            deviceId = carryOver.getString("deviceId");
            displayDeviceName(deviceId);
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
            //TODO:  call update device info fragment
        }
        if(id == R.id.disable_device) {
            //TODO:  call disable device
            // return to home
        }
        if(id == R.id.remove_device) {
            //TODO:  call remove device
            // return to home
        }
        return super.onOptionsItemSelected(item);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void displayDeviceName(String deviceId) {
        DatabaseReference deviceRef = dB.getDeviceChild(deviceId).child("deviceName");

        deviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deviceName.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });
    }

    private void displayDeviceInfo(String deviceId) {
        DatabaseReference deviceRef = dB.getDeviceChild(deviceId).child("sensors");

        deviceRef.addValueEventListener(new ValueEventListener() {
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
    public boolean onNavigateUp() {
        finish();
        return true;
    }
}
