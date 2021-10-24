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

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    protected Button loginButton, signupButton;
    protected EditText usernameET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // auth = FirebaseAuth.getInstance();

        // usernameET = (EditText) findViewById(R.id.usernameInput);
        // passwordET = (EditText) findViewById(R.id.passwordInput);

        // signupButton = (Button) findViewById(R.id.signUpPage);
        // loginButton = (Button) findViewById(R.id.loginButton);
        // loginButton.setOnClickListener(view -> userLogin());

        // signupButton.setOnClickListener(view -> openSignupActivity());
    }
}