package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SensorActivity extends AppCompatActivity {

    // Initialize variables
    private final Database dB = new Database();

    protected TextView sensorName;
    protected String sensorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorName = (TextView) findViewById(R.id.sensor_name);

        Bundle carryOver = getIntent().getExtras();
        if (carryOver != null) {
            sensorId = carryOver.getString("sensorId");
            displaySensorInfo(sensorId);
        } else {
            Toast.makeText(this, "Error fetching device", Toast.LENGTH_LONG).show();
            openHomeActivity();
        }
    }

    private void displaySensorInfo(String sensorId) {
        DatabaseReference sensorRef = dB.getSensorChild(sensorId);

        sensorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sensorName.setText(snapshot.child("sensorName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }
        });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}