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
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    protected TextView welcomeUserMessage;
    protected Button addNewDeviceButton, logoutButton;
    protected ListView deviceList;

    public String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //TODO: Help Get Authenticated User's Name
        userName = "User";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

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

        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);

        deviceList = (ListView) findViewById(R.id.deviceDataList);
        // TODO: Call loadDeviceView(...)

        deviceList.setOnItemClickListener((parent, view, position, id) -> {
            // TODO: Navigate to Sensor Activity of Selected Profile By Id
            //goToSensorActivity(...);
        });

        addNewDeviceButton = findViewById(R.id.addDeviceButton);
        addNewDeviceButton.setOnClickListener(view -> {
            DeviceFragment dialog = new DeviceFragment();
            dialog.show(getSupportFragmentManager(), "Add Device Fragment");
        });

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> logoutUser());

    }

    // Get, Initialize, and Update Devices
    protected void loadDeviceView(boolean orderByName) {        // Display List of Devices
        List<String> devices = new ArrayList<>();

        //TODO: Get List of Devices from DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref = db.getReference("Devices")

        //TODO: Format List from DB for Adapter

        // Add Devices to ListView
        deviceList.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, devices));
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

    // Navigation to Add Device Activity
    private void goToAddDeviceActivity() {
        //Intent intent = new Intent(this, SensorActivity.class);
        //startActivity(intent);
    }

    // Navigation to Add Device Activity
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}