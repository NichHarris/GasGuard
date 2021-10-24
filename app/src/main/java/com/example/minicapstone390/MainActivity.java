package com.example.minicapstone390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private View loginView, signupView;
    protected FirebaseDatabase db;
    protected DatabaseReference ref;
    protected FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DAOSensor dao = new DAOSensor();
//        Sensor sensor = new Sensor(2, 3.5, "1998-12-12 11:11", "temp");
//        String key = dao.add(sensor);
//        System.out.println(key);
//
//        Device device = new Device("3", "Test2", false);
//        DAODevice daoDevice = new DAODevice();
//        daoDevice.add(device);


        // get the current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // condition is user is null on loading screen
        if (user == null) {
            openLoginActivity();
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}