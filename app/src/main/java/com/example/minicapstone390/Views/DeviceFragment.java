package com.example.minicapstone390.Views;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
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
import java.util.UUID;

// Device Fragment
public class DeviceFragment extends DialogFragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    private static final String DEVICES = DatabaseEnv.USERDEVICES.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;

    protected BluetoothSocket btSocket;
    protected BluetoothAdapter btAdapter;
    protected BluetoothDevice btDevice;
    protected UUID uuid;
    private boolean btConnectionState = false;
    protected Button cancelButton, saveButton;
    protected EditText wifiNameInput, wifiPasswordInput;
    protected PopupWindow popupWindow;
    public HomeActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (HomeActivity) activity;
    }

    // TODO: Replace with check for device ID in database and add it to user
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_device, container, false);

        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(getContext());
        // TODO CHECK THEME

        uuid = UUID.fromString(sharePreferenceHelper.getUUID());
        
        // Input Fields for Student Profile Data
        wifiNameInput = (EditText) view.findViewById(R.id.wifi_name);
        wifiPasswordInput = (EditText) view.findViewById(R.id.wifi_password);

        View popupView = inflater.inflate(R.layout.popup_window, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        //Create Object and Listener for Cancel and Save Buttons
        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);

        cancelButton.setOnClickListener(v -> dismiss());

        /*
            TODO FOR BLUETOOTH
            - Need to search for local devices when the CONNECT Button is clicked.
            - Verify the user has entered password and name credentials when connect is clicked, if not request focus
            - If a device is not found send a toast
            - If a device is found, send a toast attempting to connect
            - If the wifi connection fails, send a toast and request focus on name/password
            - If the wifi connection succeeds, send back the device ID and a passed status
            - Verify the user doesn't already have a connection to the device
            - Set the device location based on the bluetooth fine location
        */
        saveButton.setOnClickListener(view1 -> {
            // Get Input Responses
            String wifiName = wifiNameInput.getText().toString();
            String wifiPassword = wifiPasswordInput.getText().toString();

            // Validate Inputs
            if (wifiName.equals("")) {
                wifiNameInput.setError("Please enter a wifi name");
                wifiNameInput.requestFocus();
                return;
            }
            if (wifiPassword.equals("")) {
                wifiPasswordInput.setError("Please enter a wifi password");
                wifiPasswordInput.requestFocus();
                return;
            }

            btAdapter = BluetoothAdapter.getDefaultAdapter();


            // 1) All Inputs Must Be Filled
            if (deviceId.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Must Fill All Input Fields!", Toast.LENGTH_LONG).show();
            } else {

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
                        } else {
                            Log.e(TAG, "No such device ID exists");
                            Toast.makeText(activity, "No such device ID exists", Toast.LENGTH_SHORT).show();
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