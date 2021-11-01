package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    protected ListView sensorList;
    protected TextView deviceName; //TODO
    protected List<String> sensorIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

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

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void displayDeviceInfo(String deviceId) {
        DatabaseReference deviceRef = dB.getDeviceChild(deviceId).child("sensors");

        deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> sensorIds = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    sensorIds.add(ds.getValue(String.class));
                    addToSensorList(ds.getValue(String.class));
                }

                sensorList.setOnItemClickListener((parent, view, position, id) -> {
                    // TODO: Navigate to Sensor Activity of Selected Profile By Id
                    System.out.println(sensorIds.get(position));
                    goToSensorActivity(sensorIds.get(position));
                });

                getSensorNames(sensorIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });

        System.out.println("Here" + sensorIds);
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
}