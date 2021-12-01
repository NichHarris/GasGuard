package com.example.minicapstone390.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.Models.User;
import com.example.minicapstone390.R;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;
    protected Button signUpButton, loginButton;
    protected EditText usernameET, emailET, passwordET, confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(SignupActivity.this);

        // Set theme
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize EditTexts
        usernameET = (EditText) findViewById(R.id.username);
        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPassword);

        // Sign Up And Continue to Home Page on Success
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(view -> userSignup());

        // Switch to Login Page
        loginButton = (Button) findViewById(R.id.loginPage);
        loginButton.setOnClickListener(view -> openLoginActivity());
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

    // Sign up a user
    private void userSignup() {
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPass = confirmPasswordET.getText().toString();

        // Validation
        // (1) All Inputs Must Be Filled
        boolean isValid = true;
        if (username.equals("")) {
            usernameET.setError("Username Must Be Entered!");
            usernameET.requestFocus();
            isValid = false;
        }

        if (email.equals("")) {
            emailET.setError("Email Must Be Entered!");
            emailET.requestFocus();
            isValid = false;
        }

        if (password.equals("")) {
            passwordET.setError("Password Must Be Entered!");
            passwordET.requestFocus();
            isValid = false;
        }
        if (confirmPass.equals("")) {
            confirmPasswordET.setError("Password Must Be Entered!");
            confirmPasswordET.requestFocus();
            isValid = false;
        }

        if (isValid) {
            // (2) Email Must Be a Valid Email and Unique
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Email Must Be Valid!");
                emailET.requestFocus();
                return;
            }

            // (3) Password Must Be at Least 8 Characters with at least one Capitalized, one Number, one Special Character
            // Regex for Password: Must Contain At Least 1 Lower and 1 Upper Case Character, 1 Number, 1 Special Characters, and Be 8 Characters Long
            String passwordRegex = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[`~!@#$%^&*()\\-=_+\\[\\]\\\\{}|;:'\",.\\/<>? ]).{8,}$";
            Pattern pattern = Pattern.compile(passwordRegex);
            if (!pattern.matcher(password).matches()) {
                passwordET.setError("Password Must Be Solid!");
                passwordET.requestFocus();
                return;
            }

            // (4) Password and Confirm Password Must Match
            if (!password.equals(confirmPass)) {
                confirmPasswordET.setError("Passwords Must Match!");
                confirmPasswordET.requestFocus();
                return;
            }

            // Create User using Firebase Auth
            dB.getAuth().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = new User(username, email);

                            // Get current User Id
                            String currentUserId = Objects.requireNonNull(dB.getAuth().getCurrentUser()).getUid();

                            DatabaseReference userRef = dB.getUserRef();

                            // Also Add User to Realtime DB And Open Home Activity on Success
                            userRef.child(currentUserId).setValue(user)
                                    .addOnCompleteListener(t -> {
                                        if (t.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, String.format("User: %s successfully registered.", username));
                                            openHomeActivity();
                                        } else {
                                            Log.e(TAG, "Failed to create user on database");
                                            Toast.makeText(getApplicationContext(), "Failed to Register User!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Email is already registered");
                            emailET.setError("Email is already registered!");
                            emailET.requestFocus();
                        }
                    });
        }
    }

    // Navigate to HomeActivity
    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Navigate to LoginActivity
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        //Remove transition
        overridePendingTransition(0, 0);
    }
}