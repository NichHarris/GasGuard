package com.example.minicapstone390;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Device Fragment
public class DeviceFragment extends DialogFragment {
    protected Button cancelButton, saveButton;
    protected EditText deviceIdInput, deviceNameInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_device, container, false);

        // Input Fields for Student Profile Data
        deviceIdInput = (EditText) view.findViewById(R.id.device_id);
        deviceNameInput = (EditText) view.findViewById(R.id.device_name);

        //Create Object and Listener for Cancel and Save Buttons
        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(view1 -> {
            // Get Input Responses
            String deviceName = deviceNameInput.getText().toString();

            // Validate Inputs
            // 1) All Inputs Must Be Filled
            if (deviceName.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Must Fill All Input Fields!", Toast.LENGTH_LONG).show();
            } else {
                // Get Users DB Reference
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                assert currentUser != null;

                Device device = new Device(currentUser.getUid(), deviceName, true);
                // add the device, then add its deviceId to the user
                DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("Devices");
                devicesRef.push().setValue(device);
                // TODO: add some try to catch error cases
                String deviceKey = devicesRef.getKey();
                // TODO: add device to the sensors that are part of the device
                
                // Close Fragment
                dismiss();
            }
        });
        return view;
    }
}