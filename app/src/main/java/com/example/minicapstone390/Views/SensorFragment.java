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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// Device Fragment
public class SensorFragment extends DialogFragment {
    private static final String TAG = "SensorFragment";
    private static final String SENSORNAME = DatabaseEnv.SENSORNAME.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected Button cancelButton, saveButton;
    protected RadioGroup sensorTypeOptions;
    protected EditText sensorNameInput;
    protected String sensorId;

    // TODO: Replace with check for device ID in database and add it to user
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_sensor, container, false);

        sensorId = getArguments().getString("id");

        if (sensorId != null) {
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
                if (sensorName.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Must Fill All Input Fields!", Toast.LENGTH_LONG).show();
                } else {
                    updateSensor(sensorName);
                }
            });
        }
        return view;
    }

    private void updateSensor(String name) {
        dB.getSensorChild(sensorId).child(SENSORNAME).setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Sensor name was not able to be updated");
                }
            }
        });

        boolean type = true;
        if (sensorTypeOptions.getCheckedRadioButtonId() == R.id.gasOutput) {
            type = false;
        }
        // TODO
        if (type) {
            Log.d(TAG, "Set sensor type as normal");
        } else {
            Log.d(TAG, "Set sensor type as gas type");
        }
        dismiss();
    }

}