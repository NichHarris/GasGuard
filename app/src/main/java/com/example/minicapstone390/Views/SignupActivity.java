package com.example.minicapstone390.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Models.User;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    // Initialize variables
    private final Database dB = new Database();
    private final String passwordRegex = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[`~!@#$%^&*()\\-=_+\\[\\]\\\\{}|;:'\",.\\/<>? ]).{8,}$";

    protected Button signUpButton, loginButton;
    protected TextView forgotPassword;
    protected EditText usernameET, emailET, passwordET, confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        // Send password reset email
         forgotPassword = (TextView) findViewById(R.id.signUpForgot);
         forgotPassword.setOnClickListener(view -> sendReset());
    }

    private void userSignup() {
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPass = confirmPasswordET.getText().toString();

        // Validation
        // (1) All Inputs Must Be Filled
        if (username.equals("") || email.equals("") || password.equals("") || confirmPass.equals("")) {
            Toast.makeText(getApplicationContext(), "All Inputs Must Be Filled!", Toast.LENGTH_LONG).show();
            return;
        }

        // (3) Email Must Be a Valid Email and Unique
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Email Must Be Valid!");
            emailET.requestFocus();
            return;
        }

        // (4) Password Must Be at Least 8 Characters with at least one Capitalized, one Number, one Special Character
        // Regex for Password: Must Contain At Least 1 Lower and 1 Upper Case Character, 1 Number, 1 Special Characters, and Be 8 Characters Long
        Pattern pattern = Pattern.compile(passwordRegex);
        if (!pattern.matcher(password).matches()) {
            passwordET.setError("Password Must Be Solid!");
            passwordET.requestFocus();
            return;
        }

        // (5) Password and Confirm Password Must Match
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
                                    openHomeActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to Register User!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    emailET.setError("Email is already registered!");
                    emailET.requestFocus();
                }
            });
    }

    private void sendReset() {
        String email = emailET.getText().toString();

        if (email.equals("")) {
            emailET.setError("Please enter email to send reset to");
            emailET.requestFocus();
            return;
        }

        dB.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Password reset sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error sending password reset, email is not associated with an account!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}