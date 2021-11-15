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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.Models.Sensor;
import com.example.minicapstone390.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Device Fragment
public class SensorFragment extends DialogFragment {
    private static final String TAG = "SensorFragment";

    // Declare variables
    protected Button cancelButton, saveButton;
    protected RadioGroup sensorTypeOptions;
    protected EditText sensorNameInput;

    // TODO: Replace with check for device ID in database and add it to user
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_device, container, false);

        // Input Fields for Student Profile Data
        sensorNameInput = (EditText) view.findViewById(R.id.sensorName);
        sensorTypeOptions = (RadioGroup) view.findViewById(R.id.sensorOptions);

        //Create Object and Listener for Cancel and Save Buttons
        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(view1 -> {
            // Get Input Responses
            String sensorName = sensorNameInput.getText().toString();

            // Validate Inputs
            // 1) All Inputs Must Be Filled
            if (sensorName.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Must Fill All Input Fields!", Toast.LENGTH_LONG).show();
            } else {
                                // Close Fragment
                dismiss();
            }
        });
        return view;
    }

}