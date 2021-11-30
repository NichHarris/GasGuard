package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Models.DatabaseEnv;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String USERNAME = DatabaseEnv.USERNAME.getEnv();
    private static final String USEREMAIL = DatabaseEnv.USEREMAIL.getEnv();
    private static final String USERFIRST = DatabaseEnv.USERFIRST.getEnv();
    private static final String USERLAST = DatabaseEnv.USERLAST.getEnv();
    private static final String USERPHONE = DatabaseEnv.USERPHONE.getEnv();

    // Declare variables
    private final Database dB = new Database();

    protected SharedPreferenceHelper sharePreferenceHelper;
    protected TextView profileName, profileEmail, profilePhone, profileFirstName, profileLastName;
    protected Toolbar toolbar;
    public String userName, userEmail, userPhone, userFirstName, userLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(ProfileActivity.this);

        // Set theme
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize TextViews
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileName = (TextView) findViewById(R.id.profile_name);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        profileFirstName = (TextView) findViewById(R.id.profileFirstName);
        profileLastName = (TextView) findViewById(R.id.profileLastName);

        // Update user info page
        updateAllInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTheme();
    }

    public void setTheme() {
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }
    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.theme);
        if (sharePreferenceHelper.getTheme()) {
            menuItem.setTitle("Disable Dark Mode");
        }
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.update_info:
                UpdateInfoFragment infoFragment = new UpdateInfoFragment();
                infoFragment.show(getSupportFragmentManager(), "UpdateInfoFragment");
                updateAllInfo();
                break;
            case R.id.theme:
                if (sharePreferenceHelper.getTheme()) {
                    sharePreferenceHelper.setTheme(false);
                } else {
                    sharePreferenceHelper.setTheme(true);
                }
                setDropDownText(item);
                reload();
                break;
            case R.id.logout_user:
                logoutUser();
                break;
            case R.id.remove_user:
                deleteUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDropDownText(MenuItem item) {
        if(sharePreferenceHelper.getTheme()) {
            item.setTitle("Disable Dark Mode");
        } else {
            item.setTitle("Enable Dark Mode");
        }
    }

    // Delete user
    private void deleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Account Confirmation");
        builder.setMessage("Deleting user account will completely remove the associated account and its owned devices.");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dB.getUserChild().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dB.getUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Log.d(TAG, "Delete user failed");
                                                        }
                                                    }
                                                });
                                                goToLoginActivity();
                                            } else {
                                                Log.e(TAG, "Task failed");
                                            }
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "No user found");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError e) {
                                Log.d(TAG, e.toString());
                                throw e.toException();
                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Cancelled Delete Account");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Reload page
    private void reload() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Navigation to Add Device Activity
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        goToLoginActivity();
    }

    // Navigate to long activity
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Navigate to long activity
    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Update all user info
    public void updateAllInfo() {
        dB.getUserChild(dB.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child(USERNAME).exists() ? snapshot.child(USERNAME).getValue(String.class) : "";
                    userEmail = snapshot.child(USEREMAIL).exists() ? snapshot.child(USEREMAIL).getValue(String.class) : "";
                    userPhone = snapshot.child(USERPHONE).exists() ? snapshot.child(USERPHONE).getValue(String.class) : "";
                    userFirstName = snapshot.child(USERFIRST).exists() ? snapshot.child(USERFIRST).getValue(String.class) : "";
                    userLastName = snapshot.child(USERLAST).exists() ? snapshot.child(USERLAST).getValue(String.class) : "";
                    updateProfile();
                } else {
                    Log.d(TAG, "Unable to get user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.d(TAG, e.toString());
                throw e.toException();
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
        goToHomeActivity();
        return true;
    }

    // Navigate back to homepage on back pressed
    @Override
    public void onBackPressed() {
        goToHomeActivity();
    }
}