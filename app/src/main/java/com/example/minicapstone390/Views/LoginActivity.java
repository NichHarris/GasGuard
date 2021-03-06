package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected Button loginButton, signupButton;
    protected TextView forgotPassword;
    protected EditText userEmailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(LoginActivity.this);

        // Set theme
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmailET = (EditText) findViewById(R.id.usernameInput);
        passwordET = (EditText) findViewById(R.id.passwordInput);

        // Login And Continue to Home Page on Success
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> userLogin());

        // Switch to Sign Up Page
        signupButton = (Button) findViewById(R.id.signUpPage);
        signupButton.setOnClickListener(view -> openSignupActivity());

        // Send password reset email
        forgotPassword = (TextView) findViewById(R.id.loginForgot);
        forgotPassword.setOnClickListener(view -> sendReset());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTheme();
    }

    // Set theme
    public void setTheme() {
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }
    }

    // Send an email password reset to the user
    private void sendReset() {
        String userEmail = userEmailET.getText().toString();

        // Validate email
        if (userEmail.equals("")) {
            userEmailET.setError("Please enter email to send reset to");
            passwordET.setEnabled(false);
            passwordET.setBackgroundColor(Color.DKGRAY);
            userEmailET.requestFocus();
            return;
        }

        // Sent reset
        dB.getAuth().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, String.format("Password reset sent to: %s", userEmail));
                    Toast.makeText(getApplicationContext(), "Password reset sent successfully!", Toast.LENGTH_SHORT).show();
                    reload();
                } else {
                    Log.e(TAG, "Failed to send password reset email");
                    Toast.makeText(getApplicationContext(), "Error sending password reset, email is not associated with an account!", Toast.LENGTH_SHORT).show();
                    userEmailET.requestFocus();
                }
            }
        });
    }

    // Handles user login
    private void userLogin() {
        String userEmail = userEmailET.getText().toString();
        String password = passwordET.getText().toString();

        // Validation
        // (1) All Inputs Must Be Filled
        boolean isValid = true;
        if (userEmail.equals("")) {
            userEmailET.setError("Email Must Be Entered!");
            userEmailET.requestFocus();
            isValid = false;
        }

        if (password.equals("")) {
            passwordET.setError("Password Must Be Entered!");
            passwordET.requestFocus();
            isValid = false;
        }

        if (isValid) {
            // Login User Using Firebase Auth
            dB.getAuth().signInWithEmailAndPassword(userEmail, password)
                    .addOnCompleteListener(this, task -> {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "Login attempt failed");
                            Toast.makeText(getApplicationContext(), "Login failed. Try again!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.i(TAG, String.format("Successfully logged in user: %s", userEmail));
                            Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                            openHomeActivity();
                        }
                    });
        }
    }

    // Navigate to home activity
    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Reload page
    private void reload() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        //Remove transition
        overridePendingTransition(0, 0);
    }

    // Navigate to signup activity
    private void openSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        
        //Remove transition
        overridePendingTransition(0, 0);
    }
}