package com.example.minicapstone390.Views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.example.minicapstone390.R;
import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Declare variables
    private final Database dB = new Database();
    private TelephonyManager telephonyManager;
    protected SharedPreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(MainActivity.this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
                    sharePreferenceHelper.setUUID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
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