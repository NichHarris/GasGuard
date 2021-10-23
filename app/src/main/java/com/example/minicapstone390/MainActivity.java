package com.example.minicapstone390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

        db = FirebaseDatabase.getInstance();
        ref =db.getReference("sensors");

        // get the current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // condition is user is null on loading screen
        if (user == null) {
            // go to login
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapS: snapshot.getChildren()) {
                    Object value = snapS.child("value").getValue();
                    Object date = snapS.child("date").getValue();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loginView = findViewById(R.id.loginPage);

    //  loginView.setOnClickListener();
    }
}