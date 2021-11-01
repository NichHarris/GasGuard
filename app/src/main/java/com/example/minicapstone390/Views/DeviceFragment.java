package com.example.minicapstone390.Views;

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

import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Device Fragment
public class DeviceFragment extends DialogFragment {

    private final FirebaseDatabase dB = FirebaseDatabase.getInstance();;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    protected Button cancelButton, saveButton;
    protected EditText deviceIdInput, deviceNameInput;

    public int deviceCount;
    public String deviceKey;

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
                FirebaseUser currentUser = auth.getCurrentUser();
                assert currentUser != null;

                Device device = new Device(deviceName, true);

                // add the device, then add its deviceId to the user
                DatabaseReference devicesRef = dB.getReference("Devices").push();
                devicesRef.setValue(device);

                // TODO: add some try to catch error cases
                deviceKey = devicesRef.getKey();

                // TODO: add device to the sensors that are part of the device
                DatabaseReference userRef = dB.getReference("Users").child(currentUser.getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        
                        deviceCount = (int) snapshot.getChildrenCount();
                        // for updating users with a device

                        Map<String, Object> keys = new HashMap<>();
                        keys.put(Integer.toString(deviceCount), deviceKey);
                        userRef.child("devices").updateChildren(keys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dismiss();
                    }
                });
                // Close Fragment
                dismiss();
            }
        });
        return view;
    }

}