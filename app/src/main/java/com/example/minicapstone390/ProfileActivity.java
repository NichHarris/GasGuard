package com.example.minicapstone390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    protected FirebaseDatabase dB = FirebaseDatabase.getInstance();
    protected FirebaseAuth auth = FirebaseAuth.getInstance();
    protected String userId;
    protected TextView profileName, profileEmail;
    public String userName, userEmail, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileName = (TextView) findViewById(R.id.profile_name);

        userId = auth.getUid();
        DatabaseReference userRef = dB.getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
                userEmail = snapshot.child("userEmail").getValue(String.class);
                updateProfile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile() {
        profileName.setText(userName);
        profileEmail.setText(userEmail);
    }
}