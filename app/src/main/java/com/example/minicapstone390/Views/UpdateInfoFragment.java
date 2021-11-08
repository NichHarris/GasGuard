package com.example.minicapstone390.Views;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.Models.User;
import com.example.minicapstone390.Views.ProfileActivity;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Device Fragment
public class UpdateInfoFragment extends DialogFragment {

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

            DatabaseReference userRef = dB.getUserChild(dB.getUserId());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName, userEmail, userPhone, userFirstName, userLastName;
                    userName = userNameInput.getText().toString().equals("") ? snapshot.child("userName").getValue(String.class) : userNameInput.getText().toString();
                    userEmail = userEmailInput.getText().toString().equals("") ? snapshot.child("userEmail").getValue(String.class) : userEmailInput.getText().toString();
                    userPhone = userPhoneInput.getText().toString().equals("") ? snapshot.child("userPhone").getValue(String.class) : userPhoneInput.getText().toString();
                    userFirstName = userFirstNameInput.getText().toString().equals("") ? snapshot.child("userFirstName").getValue(String.class) : userFirstNameInput.getText().toString();
                    userLastName = userLastNameInput.getText().toString().equals("") ? snapshot.child("userLastName").getValue(String.class) : userLastNameInput.getText().toString();

                    DatabaseReference devicesRef = userRef.child("devices");

                    devicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> devices = new HashMap<>();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                devices.put(ds.getKey(), ds.getValue(String.class));
                            }

                            // Can add validation after
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("userName", userName);
                            userInfo.put("userEmail", userEmail);
                            userInfo.put("userPhone", userPhone);
                            userInfo.put("userFirstName", userFirstName);
                            userInfo.put("userLastName", userLastName);
                            userInfo.put("devices", devices);
                            userRef.updateChildren(userInfo);

                            //noinspection ConstantConditions
                            // TODO: Causing profile to crash
                            ((ProfileActivity)requireActivity()).updateAllInfo();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dismiss(); // TODO: Add error catch
                        }
                    });
                    dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dismiss(); // TODO: Add error catch
                }
            });
        });
        return view;
    }
}