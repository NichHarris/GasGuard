package com.example.minicapstone390.Views;

import android.os.Bundle;
import android.util.Log;
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
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// Device Fragment
public class UpdateDeviceFragment extends DialogFragment {
    private static final String TAG = UpdateDeviceFragment.class.getSimpleName();
    private static final String DEVICENAME = DatabaseEnv.DEVICENAME.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected Button cancelButton, saveButton;
    protected EditText deviceNameInput;
    protected String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_update_device, container, false);

        deviceId = getArguments().getString("id");

        if (deviceId != null) {
            // Input Fields for Student Profile Data
            deviceNameInput = (EditText) view.findViewById(R.id.update_device_name);

            //Create Object and Listener for Cancel and Save Buttons
            cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
            saveButton = (Button) view.findViewById(R.id.save_add_device_button);

            cancelButton.setOnClickListener(v -> dismiss());

            saveButton.setOnClickListener(view1 -> {
               String deviceName = deviceNameInput.getText().toString();
               if (deviceName.isEmpty()) {
                   Toast.makeText(getActivity().getApplicationContext(), "Must Fill All Input Fields!", Toast.LENGTH_LONG).show();
               } else {
                   updateDevice(deviceName);
               }
            });
        }
        return view;
    }


    private void updateDevice(String deviceName) {
        dB.getDeviceChild(deviceId).child(DEVICENAME).setValue(deviceName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Sensor name was not able to be updated");
                }
            }
        });
        dismiss();
    }
}