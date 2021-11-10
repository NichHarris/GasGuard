package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;

public class HomeActivity extends AppCompatActivity {

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected TextView welcomeUserMessage;
    protected Toolbar toolbar;
    protected ListView deviceList;
    protected List<String> deviceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharePreferenceHelper = new SharedPreferenceHelper(HomeActivity.this);
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceIds = new ArrayList<>();

        updatePage();

        //TODO: check if devices are part of the user
        //TODO: Put id list in sharedpred to avoid out of scope issue (asynch issue)
//        DatabaseReference deviceRef = dB.getDeviceRef().child("-Mmp8L5ajMh3q6W8wcnm");
//        deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                System.out.println(snapshot.child("deviceName").getValue(String.class));
//                if (snapshot.child("userId").getValue(String.class).equals(dB.getUserId())) {
//                    //System.out.println(userId);
//                } else {
//                    //System.out.println(snapshot.child("userId").getValue(String.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // TODO: Add error catch
//            }
//        });

        welcomeUserMessage = (TextView) findViewById(R.id.welcomeUserMessage);

        deviceList = (ListView) findViewById(R.id.deviceDataList);
        loadDeviceList();
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
        int id = item.getItemId();
        // TODO: Convert to switch
        if(id == R.id.add_device) {
            //TODO:  call add device fragment
        }
        if(id == R.id.profile) {
            goToProfileActivity();
        }
        //NOTE: DON'T IMPLEMENT FOR NOW
        if(id == R.id.device_names) {
            //TODO: change list of device names to set names
        }
        return super.onOptionsItemSelected(item);
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
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });
    }

    //Update User Message
    private void updatePage() {
        dB.getUserChild(dB.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String defaultMessage = getResources().getString(R.string.welcome_user).replace("{0}", snapshot.child("userName").getValue(String.class));
                welcomeUserMessage.setText(defaultMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
        loadDeviceList();
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Navigation to Sensor Activity
    private void goToDeviceActivity(String deviceId) {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }

    private void getDeviceNames(List<String> devices) {
        List<String> deviceNames = new ArrayList<>();

        for (String id: devices) {
            //TODO: check if devices are part of the user
            DatabaseReference deviceRef = dB.getDeviceRef().child(id);
            deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    deviceNames.add(snapshot.child("deviceName").getValue(String.class));
                    setDeviceList(deviceNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // TODO: Add error catch
                }
            });
        }
        setDeviceList(deviceNames);
    }

    // Navigation to Add Device Activity
    private void logoutUser() {
        dB.getAuth().signOut();
        goToLoginActivity();
    }

    private void setDeviceList(List<String> devices) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        // Add Devices to ListView
        deviceList.setAdapter(adapter);
    }

    public void addToDeviceList(String id) {
        deviceIds.add(id);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}