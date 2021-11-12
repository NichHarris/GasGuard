package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        sharePreferenceHelper = new SharedPreferenceHelper(LoginActivity.this);
        // Set theme
        if (sharePreferenceHelper.getTheme()) {
            setTheme(R.style.NightMode);
        } else {
            setTheme(R.style.LightMode);
        }

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

    private void sendReset() {
        String userEmail = userEmailET.getText().toString();

        if (userEmail.equals("")) {
            userEmailET.setError("Please enter email to send reset to");
            userEmailET.requestFocus();
            return;
        }

        dB.getAuth().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, String.format("Password reset sent to: %s", userEmail));
                    Toast.makeText(getApplicationContext(), "Password reset sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to send password reset email");
                    Toast.makeText(getApplicationContext(), "Error sending password reset, email is not associated with an account!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userLogin() {
        String userEmail = userEmailET.getText().toString();
        String password = passwordET.getText().toString();

        // Validation
        // (1) All Inputs Must Be Filled
        if (userEmail.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "All Inputs Must Be Filled!", Toast.LENGTH_LONG).show();
            return;
        }

        // Login User Using Firebase Auth
        dB.getAuth().signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG,"Login attempt failed");
                    Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                    openHomeActivity();
                } else {
                    Log.i(TAG,String.format("Successfully logged in user: %s", userEmail));
                    Toast.makeText(getApplicationContext(), "Login failed. Try again!", Toast.LENGTH_LONG).show();
                }
        });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}