package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    // Initialize variables
    private final Database dB = new Database();

    protected TextView profileName, profileEmail, profilePhone, profileFirstName, profileLastName;
    protected Button updateInfo;
    public String userName, userEmail, userPhone, userFirstName, userLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileName = (TextView) findViewById(R.id.profile_name);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        profileFirstName = (TextView) findViewById(R.id.profileFirstName);
        profileLastName = (TextView) findViewById(R.id.profileLastName);

        updateInfo = (Button) findViewById(R.id.profileUpdateButton);
        updateInfo.setOnClickListener(view -> {
            UpdateInfoFragment dialog = new UpdateInfoFragment();
            dialog.show(getSupportFragmentManager(), "Update Info");
        });

        DatabaseReference userRef = dB.getUserChild(dB.getUserId());
        updateAllInfo(userRef);
    }

    public void updateAllInfo(DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
                userEmail = snapshot.child("userEmail").getValue(String.class);
                userPhone = snapshot.child("userPhone").getValue(String.class);
                userFirstName = snapshot.child("userFirstName").getValue(String.class);
                userLastName = snapshot.child("userLastName").getValue(String.class);
                updateProfile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Add error catch
            }

            private void updateProfile() {
                profileName.setText(String.format("Username: %s", userName));
                profileEmail.setText(String.format("Email: %s", userEmail));
                profilePhone.setText(String.format("Phone Number: %s", userPhone));
                profileFirstName.setText(String.format("First Name: %s", userFirstName));
                profileLastName.setText(String.format("Last Name: %s", userLastName));
            }
        });
    }
}