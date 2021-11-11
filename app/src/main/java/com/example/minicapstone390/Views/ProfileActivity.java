package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.example.minicapstone390.Views.UpdateInfoFragment;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    // Declare variables
    private final Database dB = new Database();

    protected SharedPreferenceHelper sharePreferenceHelper;
    protected TextView profileName, profileEmail, profilePhone, profileFirstName, profileLastName;
    protected Toolbar toolbar;
    public String userName, userEmail, userPhone, userFirstName, userLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharePreferenceHelper = new SharedPreferenceHelper(ProfileActivity.this);
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharePreferenceHelper = new SharedPreferenceHelper(ProfileActivity.this);
        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        updateAllInfo();

        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileName = (TextView) findViewById(R.id.profile_name);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        profileFirstName = (TextView) findViewById(R.id.profileFirstName);
        profileLastName = (TextView) findViewById(R.id.profileLastName);
    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.update_info) {
            UpdateInfoFragment dialog = new UpdateInfoFragment();
            dialog.show(getSupportFragmentManager(), "Update Info");
            updateAllInfo();
        }
        if(id == R.id.theme) {
            if (sharePreferenceHelper.getTheme()) {
                sharePreferenceHelper.setTheme(false);
            } else {
                sharePreferenceHelper.setTheme(true);
            }
            reload();
            //TODO Add transitions
        }
        if(id == R.id.update_notification) {
            NotificationsFragment dialog = new NotificationsFragment();
            dialog.show(getSupportFragmentManager(), "Notifications");
            updateAllInfo();
        }
        if(id == R.id.logout_user) {
            logoutUser();
        }
        if(id == R.id.remove_user) {
            //TODO: ADD CONFIRM OPTION FOR DELETE
            deleteUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteUser() {
        dB.getUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // TODO: send toast for successful delete
                } else {
                    // TODO: Send toast for failed delete
                }
            }
        });
    }

    private void reload() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        // TODO: Add transition
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

    public void updateAllInfo() {
        dB.getUserChild(dB.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                System.out.println(error.toString());
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

    // Navigate back to homepage on task-bar return
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}