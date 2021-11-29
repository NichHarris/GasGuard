package com.example.minicapstone390.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.R;
import com.example.minicapstone390.Views.DeviceActivity;
import com.example.minicapstone390.Views.HomeActivity;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private static final String TAG = "DeviceAdapter";
    private static final String deviceIdCall = "deviceId";
    private static final String callFunction = "callFunction";
    private static final String DELETE = "DELETE ";
    private static final String EDIT = "EDIT ";
    private static final String CALIBRATION = "CALIBRATE ";
    private static final String ACCESS = "ACCESS ";

    private final String deleteDeviceFunction = "deleteDevice()";
    private final String editDeviceFunction = "editDevice()";
    private final String calibrateDeviceFunction = "calibrateDevice()";


    // Define Context and ArrayList of Devices
    private Context mContext;
    private final ArrayList<Device> devices;

    // Define Single Device Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView editIcon, deleteIcon, calibrationIcon;
        public TextView deviceName, deviceStatus, deviceLocation;

        public ViewHolder(View v) {
            super(v);

            // Get Context to Call Function in HomeActivity
            mContext = v.getContext();

            // Initialize Values
            deviceName = (TextView) v.findViewById(R.id.deviceListName);
            deviceStatus = (TextView) v.findViewById(R.id.deviceListStatus);
            deviceLocation = (TextView) v.findViewById(R.id.deviceListLocation);
            editIcon = (ImageView) v.findViewById(R.id.deviceEditIcon);
            deleteIcon = (ImageView) v.findViewById(R.id.deviceDeleteIcon);
            calibrationIcon = (ImageView) v.findViewById(R.id.deviceCalibrationIcon);

            editIcon.setOnClickListener((view) -> {
                callActivity(editDeviceFunction, EDIT, getAdapterPosition());
            });

            deleteIcon.setOnClickListener((view) -> {
                callActivity(deleteDeviceFunction, DELETE, getAdapterPosition());
            });

            calibrationIcon.setOnClickListener((view) -> {
                callActivity(calibrateDeviceFunction, CALIBRATION, getAdapterPosition());
            });

            v.setOnClickListener((view) -> {
                int deviceIndex = getAdapterPosition();
                Log.d(TAG,ACCESS + deviceIndex);

                if(!deviceStatus.getText().toString().contains("Calibrating")) {
                    ((HomeActivity) mContext).goToDeviceActivity(deviceIndex);
                } else {
                    Toast.makeText(mContext, "Device is Calibrating! Wait for Calibration to End Before Accessing Device.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void callActivity(String function, String type, int position) {
        Intent intent = new Intent(mContext, DeviceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(deviceIdCall, devices.get(position).getId());
        bundle.putString(callFunction, function);
        intent.putExtras(bundle);
        Log.i(TAG,type + position);
        mContext.startActivity(intent);
    }

    // Define Adapter for Device List
    public DeviceAdapter(ArrayList<Device> deviceNames) {
        devices = deviceNames;
    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create View Based on Specified Layout for Holder
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Set Data to Recycler View List
        Device d = devices.get(position);

        holder.deviceName.setText(d.getDeviceName());
        holder.deviceStatus.setText(getStatusDescription(d));
        holder.deviceLocation.setText(d.getDeviceLocation());
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        return devices.size();
    }

    public int getStatusDescription(Device d) {
        if(d.getCalibration()) {
            return R.string.calibratingDeviceStatus;
        } else if(d.getStatus()) {
            return R.string.activeDeviceStatus;
        } else {
            return R.string.inactiveDeviceStatus;
        }

    }
}
