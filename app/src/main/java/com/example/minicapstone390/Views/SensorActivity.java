package com.example.minicapstone390.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SensorActivity extends AppCompatActivity {

    private final FirebaseDatabase dB = FirebaseDatabase.getInstance();;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    protected ListView sensorList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
    }
}