package com.example.minicapstone390.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Initialize variables
    private final Database dB = new Database();
    protected Button loginButton, signupButton;
    protected EditText userEmailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                    openHomeActivity();
                } else {
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