package com.example.minicapstone390.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharePreferenceHelper = new SharedPreferenceHelper(MainActivity.this);
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If Not Authenticated, Send to Login Page
        if (dB.getUser() == null) {
            openLoginActivity();
        } else {
            openHomeActivity();
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}