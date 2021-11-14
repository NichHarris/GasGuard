package com.example.minicapstone390;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.Views.HomeActivity;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    // Define ArrayList of Devices
    private ArrayList<Device> devices;
    private Context mContext;

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
                //TODO: Add Device Edit Code Here
                System.out.println("EDIT " + getAdapterPosition());
            });

            deleteIcon.setOnClickListener((view) -> {
                //TODO: Add Device Delete Code Here
                System.out.println("DELETE " + getAdapterPosition());
            });

            v.setOnClickListener((view) -> {
                int deviceIndex = getAdapterPosition();
                System.out.println("ACCESS " + deviceIndex);
                ((HomeActivity)mContext).goToDeviceActivity(deviceIndex);
            });
        }
    }

    // Define Adapter for Device List
    public DeviceAdapter(ArrayList<Device> deviceNames) {
        devices = deviceNames;
    }

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
        holder.deviceStatus.setText(d.isStatus() ? R.string.activeDeviceStatus : R.string.inactiveDeviceStatus);
        holder.deviceLocation.setText(d.getDeviceLocation());
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        return devices.size();
    }
}
