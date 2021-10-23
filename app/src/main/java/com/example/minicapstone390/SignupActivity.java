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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private Pattern pattern;
    protected Button signUpButton;
    protected EditText usernameET, emailET, passwordET, confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        usernameET = (EditText) findViewById(R.id.username);
        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPassword);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSignup();
            }
        });
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

        // (2) Username Must Be Unique

//        // (3) Email Must Be a Valid Email and Unique
//        pattern = Pattern.compile(emailRegex);
//        if (!pattern.matcher(email).matches()) {
//            Toast.makeText(getApplicationContext(), "Email Must Be Valid!", Toast.LENGTH_LONG).show();
//            return;
//        }
        // (4) Password Must Be at Least 8 Characters with at least one Capitalized, one Number, one Special Character
        pattern = Pattern.compile(passwordRegex);
        if (!pattern.matcher(password).matches()) {
            Toast.makeText(getApplicationContext(), "Password Must Be Solid!", Toast.LENGTH_LONG).show();
            return;
        }

        // (5) Password and Confirm Password Must Match
        if (!password.equals(confirmPass)) {
            Toast.makeText(getApplicationContext(), "Passwords Must Match!", Toast.LENGTH_LONG).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(username, email);

                    // Get Users from DB
                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getUid())).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Registered", Toast.LENGTH_SHORT).show();
                            openHomeActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "User failed to register", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}