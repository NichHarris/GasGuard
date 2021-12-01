package com.example.minicapstone390.Views;

import android.app.Activity;
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
import com.example.minicapstone390.Models.DatabaseEnv;
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
    private static final String DEVICELOCATION = DatabaseEnv.DEVICELOCATION.getEnv();

    // Declare variables
    private final Database dB = new Database();

    protected Button cancelButton, saveButton;
    protected EditText deviceIdInput;

    public HomeActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (HomeActivity) activity;
    }

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
                // Add the device
                dB.getDeviceChild(deviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            DatabaseReference userRef = dB.getUserChild(dB.getUserId());
                            userRef.child(DEVICES).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        if (ds.exists()) {
                                            // Don't allow duplicate devices
                                            if (ds.getKey().equals(deviceId)) {
                                                Log.i(TAG, "User already owns that device");
                                                Toast.makeText(activity, String.format("User already has device: %s", deviceId), Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            }
                                        } else {
                                            Log.e(TAG, "Unable to locate device");
                                            dismiss();
                                        }
                                    }

                                    String url = "https://tools.keycdn.com/geo";



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
                            // Update location
                            if (snapshot.child(DEVICELOCATION).exists()) {
                                if (snapshot.child(DEVICELOCATION).getValue().equals("")) {
                                    snapshot.child(DEVICELOCATION).getRef().setValue("No location set").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.d(TAG, "Unable to add location");
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.e(TAG, "No such device ID exists");
                            Toast.makeText(activity, "No such device ID exists", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
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