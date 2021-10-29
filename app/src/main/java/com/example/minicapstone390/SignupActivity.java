package com.example.minicapstone390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase dB = FirebaseDatabase.getInstance();

    private final String passwordRegex = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[`~!@#$%^&*()\\-=_+\\[\\]\\\\{}|;:'\",.\\/<>? ]).{8,}$";

    protected Button signUpButton, loginButton;
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
            Toast.makeText(getApplicationContext(), "Email Must Be Valid!", Toast.LENGTH_LONG).show();
            return;
        }

        // (4) Password Must Be at Least 8 Characters with at least one Capitalized, one Number, one Special Character
        // Regex for Password: Must Contain At Least 1 Lower and 1 Upper Case Character, 1 Number, 1 Special Characters, and Be 8 Characters Long
        Pattern pattern = Pattern.compile(passwordRegex);
        if (!pattern.matcher(password).matches()) {
            Toast.makeText(getApplicationContext(), "Password Must Be Solid!", Toast.LENGTH_LONG).show();
            return;
        }

        // (5) Password and Confirm Password Must Match
        if (!password.equals(confirmPass)) {
            Toast.makeText(getApplicationContext(), "Passwords Must Match!", Toast.LENGTH_LONG).show();
            return;
        }

        // Create User using Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(username, email);

                    // Get current User Id
                    String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                    DatabaseReference userRef = dB.getReference("Users").child(currentUserId);
                    // Get Users DB Reference
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

                    // Also Add User to Realtime DB And Open Home Activity on Success
                    usersRef.child(currentUserId).setValue(user)
                            .addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    userRef.child("deviceCount").setValue("0");
                                    openHomeActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to Register User!", Toast.LENGTH_SHORT).show();
                                }
                            });
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