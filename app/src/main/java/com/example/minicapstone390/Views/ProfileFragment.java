package com.example.minicapstone390.Views;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Models.DatabaseEnv;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Device Fragment
public class ProfileFragment extends DialogFragment {
    private static final String TAG = "ProfileFragment";
    private static final String USERNAME = DatabaseEnv.USERNAME.getEnv();
    private static final String USEREMAIL = DatabaseEnv.USEREMAIL.getEnv();
    private static final String USERPHONE = DatabaseEnv.USERPHONE.getEnv();
    private static final String USERLAST = DatabaseEnv.USERLAST.getEnv();
    private static final String USERFIRST = DatabaseEnv.USERFIRST.getEnv();
    private static final String USERDEVICES = DatabaseEnv.USERDEVICES.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected Button cancelButton, saveButton;
    protected EditText userNameInput, userEmailInput, userPhoneInput, userFirstNameInput, userLastNameInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_update_info, container, false);

        // Input Fields for Student Profile Data
        userNameInput = (EditText) view.findViewById(R.id.updateUsername);
        userEmailInput = (EditText) view.findViewById(R.id.updateEmail);
        userPhoneInput = (EditText) view.findViewById(R.id.updatePhone);
        userFirstNameInput = (EditText) view.findViewById(R.id.updateFirstName);
        userLastNameInput = (EditText) view.findViewById(R.id.updateLastName);

        //Create Object and Listener for Cancel and Save Buttons
        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(view1 -> {
            // Update user info
            dB.getUserChild().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName, userEmail, userPhone, userFirstName, userLastName;
                        userName = userNameInput.getText().toString().equals("") ? (snapshot.child(USERNAME).exists() ? snapshot.child(USERNAME).getValue(String.class) : "") : userNameInput.getText().toString();

                        if (!userEmailInput.getText().toString().equals("")) {
                            // Email Must Be a Valid Email and Unique
                            if (!Patterns.EMAIL_ADDRESS.matcher(userEmailInput.getText().toString()).matches()) {
                                userEmailInput.setError("Email Must Be Valid!");
                                userEmailInput.requestFocus();
                                return;
                            } else {
                                userEmail = userEmailInput.getText().toString();
                            }
                        } else {
                            userEmail = snapshot.child(USEREMAIL).exists() ? snapshot.child(USEREMAIL).getValue(String.class) : "";
                        }
                        assert userEmail != null;
                        dB.getUser().verifyBeforeUpdateEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    userEmailInput.setError("Email already in use");
                                    userEmailInput.requestFocus();
                                }
                            }
                        });

                        // Validate entries
                        userPhone = userPhoneInput.getText().toString().equals("") ? (snapshot.child(USERPHONE).exists() ? snapshot.child(USERPHONE).getValue(String.class) : "") : userPhoneInput.getText().toString();
                        userFirstName = userFirstNameInput.getText().toString().equals("") ? (snapshot.child(USERFIRST).exists() ? snapshot.child(USERFIRST).getValue(String.class) : "") : userFirstNameInput.getText().toString();
                        userLastName = userLastNameInput.getText().toString().equals("") ? (snapshot.child(USERLAST).exists() ? snapshot.child(USERLAST).getValue(String.class) : "") : userLastNameInput.getText().toString();

                        // Get user owned devices and update user
                        dB.getUserChild().child(USERDEVICES).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Map<String, Object> devices = new HashMap<>();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (ds.exists()) {
                                        devices.put(ds.getKey(), ds.getValue(String.class));
                                    } else {
                                        Log.e(TAG, "Could not locate devices in user");
                                    }
                                }

                                // Update user with fetched info
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put(USERNAME, userName);
                                userInfo.put(USEREMAIL, userEmail);
                                userInfo.put(USERPHONE, userPhone);
                                userInfo.put(USERFIRST, userFirstName);
                                userInfo.put(USERLAST, userLastName);
                                userInfo.put(USERDEVICES, devices);
                                dB.getUserChild().updateChildren(userInfo);

                                ((ProfileActivity) getActivity()).updateAllInfo();
                                dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError e) {
                                Log.d(TAG, e.toString());
                                dismiss();
                            }
                        });
                    } else {
                        Log.d(TAG, "Unable to get user");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError e) {
                    Log.d(TAG, e.toString());
                    dismiss();
                }
            });
        });
        return view;
    }
}