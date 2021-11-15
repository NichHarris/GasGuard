package com.example.minicapstone390.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import com.example.minicapstone390.R;
import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(MainActivity.this);

        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add 5 Second Delay Before Reaching Home or Login Screens
        loadAppContent();
    }

    // Navigate to the LoginActivity
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Navigate to the HomeActivity
    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Add Delay to See Launcher Screen for 5s
    private void loadAppContent() {
        // Add 5 Second Delay Before Reaching Home or Login Screens
        Thread loadingScreenThread = new Thread() {
            @Override
            public void run() {
                try {
                    // Sleep Run for 5s (Delay Home/Main Screen Load)
                    super.run();
                    sleep(1000);
                } catch (Exception e) {
                    // Nothing to Catch
                    Log.i(TAG, e.getMessage());
                } finally {
                    // If Not Authenticated, Send to Login Page
                    if (dB.getUser() == null) {
                        Log.i(TAG, "User Not Authenticated, sending to login page.");
                        openLoginActivity();
                    } else {
                        Log.i(TAG, "User Authenticated, sending to home page");
                        openHomeActivity();
                    }
                }
            }
        };

        loadingScreenThread.start();
    }
}