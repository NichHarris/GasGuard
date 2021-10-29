package com.example.minicapstone390.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    protected FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // If Not Authenticated, Send to Login Page
        if (user == null) {
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