package com.example.minicapstone390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private final FirebaseDatabase dB = FirebaseDatabase.getInstance();;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    protected TextView welcomeUserMessage;
    protected Button addNewDeviceButton, logoutButton, testButton;
    protected ListView deviceList;
    protected String userId, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();
        DatabaseReference userRef = dB.getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
                updateUserMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        //TODO: check if devices are part of the user
        DatabaseReference deviceRef = dB.getReference("Devices").child("-Mmp8L5ajMh3q6W8wcnm");
        deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.child("deviceName").getValue(String.class));
                if (snapshot.child("userId").getValue(String.class).equals(userId)) {
                    //System.out.println(userId);
                } else {
                    //System.out.println(snapshot.child("userId").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);

        deviceList = (ListView) findViewById(R.id.deviceDataList);
        loadDeviceView(true);

        deviceList.setOnItemClickListener((parent, view, position, id) -> {
            // TODO: Navigate to Sensor Activity of Selected Profile By Id
            //goToSensorActivity(...);
        });

        // This will be changed to
        addNewDeviceButton = findViewById(R.id.addDeviceButton);
        addNewDeviceButton.setOnClickListener(view -> {
            DeviceFragment dialog = new DeviceFragment();
            dialog.show(getSupportFragmentManager(), "Add Device Fragment");
        });

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> logoutUser());
    }

    // Get, Initialize, and Update Devices - Display List of Devices
    protected void loadDeviceView(boolean orderByName) {
        //Get List of Devices from DB
        DatabaseReference usersRef = dB.getReference("Users").child(userId).child("devices");

        List<String> devices = new ArrayList<>();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> deviceIds = new ArrayList<>();
                // Format List from DB for Adapter
                for (DataSnapshot ds : snapshot.getChildren()) {
                    deviceIds.add(ds.getValue(String.class));
                }
                getDeviceNames(deviceIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        System.out.println(devices);
        devices.add("Nick");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        // Add Devices to ListView
        deviceList.setAdapter(adapter);
    }

    //Update User Message
    private void updateUserMessage() {
        String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", userName);
        welcomeUserMessage.setText(defaultMessage);
    }

    // Navigation to Sensor Activity
    private void goToSensorActivity(int deviceId) {
        //Intent intent = new Intent(this, SensorActivity.class);
        //Bundle b = new Bundle();
        //b.putInt("deviceId", deviceId);
        //intent.putExtras(b);
        //startActivity(intent);
    }

    private void getDeviceNames(List<String> devices) {
        //DatabaseReference deviceRef = dB.getReference("Devices");
        List<String> deviceNames = new ArrayList<>();

        for (String id: devices) {
            //TODO: check if devices are part of the user
            DatabaseReference deviceRef = dB.getReference("Devices").child(id);
            deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    deviceNames.add(snapshot.child("deviceName").getValue(String.class));
                    setDeviceList(deviceNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        setDeviceList(deviceNames);
    }

    // Navigation to Add Device Activity
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        goToLoginActivity();
    }

    private void setDeviceList(List<String> devices) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        // Add Devices to ListView
        deviceList.setAdapter(adapter);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}