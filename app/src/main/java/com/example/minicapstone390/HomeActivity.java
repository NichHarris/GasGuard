package com.example.minicapstone390;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity<TextEdit> extends AppCompatActivity {

    protected FirebaseAuth auth;

    protected TextView welcomeUserMessage;
    protected Button addNewDeviceButton, logoutButton;
    protected ListView deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Get Authenticated User
        String uidAuth = FirebaseAuth.getInstance().getUid();

        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);
        //String defaultMessage = getResources().getString(R.string.welcome_message);
        //String newMessage = defaultMessage.replace("{0}", user.getDisplayName());
        //welcomeUserMessage.setText(user.toString());

        deviceList = (ListView) findViewById(R.id.deviceDataList);
        // TODO: Call loadDeviceView(...)
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                // TODO: Navigate to Sensor Activity of Selected Profile By Id
                //goToSensorActivity(...);
            }
        });

    }

    // Get, Initialize, and Update Devices
    protected void loadDeviceView(boolean orderByName) {
        //TODO: Get List of Devices from DB

        // Display List of Devices
        List<String> devices = new ArrayList<>();

        //TODO: Format List from DB for Adapter

        // Add Devices to ListView
        deviceList.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, devices));
    }

    // Navigation to Sensor Activity
    private void goToSensorActivity(int deviceId) {
        //Intent intent = new Intent(this, SensorActivity.class);
        //Bundle b = new Bundle();
        //b.putInt("deviceId", deviceId);
        //intent.putExtras(b);
        //startActivity(intent);
    }
}