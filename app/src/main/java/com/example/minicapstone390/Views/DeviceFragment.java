package com.example.minicapstone390.Views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.Models.Device;
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
public class DeviceFragment extends DialogFragment {
    private static final String TAG = "AddDeviceFragment";
    private static final String DEVICES = DatabaseEnv.USERDEVICES.getEnv();
    private static final String DEVICENAME = DatabaseEnv.DEVICENAME.getEnv();
    private static final String DEVICELOC = DatabaseEnv.DEVICELOCATION.getEnv();
    private static final String DEVICESTATUS = DatabaseEnv.DEVICESTATUS.getEnv();
    // Declare variables
    private final Database dB = new Database();

    protected Button cancelButton, saveButton;
    protected EditText deviceIdInput;

    // TODO: Replace with check for device ID in database and add it to user
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_device, container, false);

        // Input Fields for Student Profile Data
        deviceIdInput = (EditText) view.findViewById(R.id.device_id);

        //Create Object and Listener for Cancel and Save Buttons
        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(view1 -> {
            // Get Input Responses
            String deviceId = deviceIdInput.getText().toString();

            // Validate Inputs
            // 1) All Inputs Must Be Filled
            if (deviceId.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Must Fill All Input Fields!", Toast.LENGTH_LONG).show();
            } else {
                //TODO: Not sure if this should be done here, probs should only be done on arduino side
                // We can then check if a device with the ID exists and add, if it doesn't we send an error
                Device device = new Device(deviceId, deviceId, "Montreal, QC", true);
                Map<String, Object> deviceAttributes = new HashMap<>();
                deviceAttributes.put(DEVICENAME, device.getDeviceName());
                deviceAttributes.put(DEVICELOC, device.getDeviceLocation());
                deviceAttributes.put(DEVICESTATUS, device.getStatus());
                dB.getDeviceChild(deviceId).updateChildren(deviceAttributes).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, String.format("Added device %s", deviceId));
                        } else {
                            Log.e(TAG, String.format("Error adding device %s", deviceId));
                            Toast.makeText(getActivity() , String.format("Unable to locate device: %s", deviceId), Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });

                // TODO: Add check if device is already part of user
                DatabaseReference userRef = dB.getUserChild(dB.getUserId());

                userRef.child(DEVICES).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            if (ds.exists()) {
                                if (ds.getKey().equals(deviceId)) {
                                    Log.i(TAG, "User already owns that device");
                                    Toast.makeText(getActivity() , String.format("User already has device: %s", deviceId), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            } else {
                                Log.e(TAG, "Unable to locate device");
                            }
                        }
                        // for updating users with a device
                        Map<String, Object> keys = new HashMap<>();
                        keys.put(deviceId, deviceId);
                        userRef.child(DEVICES).updateChildren(keys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Log.d(TAG, e.toString());
                        dismiss();
                    }
                });
                ((HomeActivity)getActivity()).updatePage();
                // Close Fragment
                dismiss();
            }
        });
        return view;
    }

}