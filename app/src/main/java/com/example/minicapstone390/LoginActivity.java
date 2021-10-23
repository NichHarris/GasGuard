package com.example.minicapstone390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    protected Button loginButton, signupButton;
    protected EditText usernameET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        usernameET = (EditText) findViewById(R.id.usernameInput);
        passwordET = (EditText) findViewById(R.id.passwordInput);

        signupButton = (Button) findViewById(R.id.signUpPage);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> userLogin());

        signupButton.setOnClickListener(view -> openSignupActivity());

    }



    private void userLogin() {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        // Validation
        // (1) All Inputs Must Be Filled
        if (username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "All Inputs Must Be Filled!", Toast.LENGTH_LONG).show();
            return;
        }

        // (2) Username Must Exist


        // (3) Password Must Match Stored Password


        // Login User Using Firebase Auth
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this, task -> {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                    openHomeActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Try again!", Toast.LENGTH_LONG).show();
                }
                }
            );
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