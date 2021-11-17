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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.R;
import com.example.minicapstone390.Views.DeviceActivity;
import com.example.minicapstone390.Views.HomeActivity;
import com.example.minicapstone390.Views.SensorFragment;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private static final String TAG = "DeviceAdapter";
    // Define Context and ArrayList of Devices
    private Context mContext;
    private final ArrayList<Device> devices;

    // Define Single Device Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView editIcon, deleteIcon;
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

            editIcon.setOnClickListener((view) -> {
                Intent intent = new Intent(mContext, DeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", devices.get(getAdapterPosition()).getId());
                bundle.putString("editDevice", "editDevice()");
                intent.putExtras(bundle);
                Log.d(TAG, "EDIT " + getAdapterPosition());
                mContext.startActivity(intent);
            });

            deleteIcon.setOnClickListener((view) -> {
                //TODO: Add Device Delete Code Here
                Log.d(TAG,"DELETE " + getAdapterPosition());
            });

            v.setOnClickListener((view) -> {
                int deviceIndex = getAdapterPosition();
                Log.d(TAG,"ACCESS " + deviceIndex);
                ((HomeActivity)mContext).goToDeviceActivity(deviceIndex);
            });
        }
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
        holder.deviceStatus.setText(d.getStatus() ? R.string.activeDeviceStatus : R.string.inactiveDeviceStatus);
        holder.deviceLocation.setText(d.getDeviceLocation());
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        return devices.size();
    }
}
