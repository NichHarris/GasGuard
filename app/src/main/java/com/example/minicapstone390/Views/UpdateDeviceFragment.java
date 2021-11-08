package com.example.minicapstone390.Views;

import android.app.FragmentManager;
import android.os.Bundle;
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
import com.example.minicapstone390.Views.ProfileActivity;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Device Fragment
public class UpdateDeviceFragment extends DialogFragment {
    private static final String TAG = "Update Device";

    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener onInputListener;

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
                        userEmail = snapshot.child("userEmail").getValue(String.class);
                    }
                    assert userEmail != null;
                    dB.getUser().verifyBeforeUpdateEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                userEmailInput.setError("Email already in use");
                                userEmailInput.requestFocus();
                                return;
                            }
                        }
                    });

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

                            ((ProfileActivity)getActivity()).updateAllInfo();
                            dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dismiss(); // TODO: Add error catch
                        }
                    });
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