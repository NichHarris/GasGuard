package com.example.minicapstone390;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.minicapstone390.Models.Device;

import java.util.ArrayList;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    // Define ArrayList of Devices
    private ArrayList<Device> devices;

    // Define Single Device Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceName, deviceStatus, deviceLocation;

        public ViewHolder(View v) {
            super(v);
            deviceName = (TextView) v.findViewById(R.id.deviceListName);
            deviceStatus = (TextView) v.findViewById(R.id.deviceListStatus);
            deviceLocation = (TextView) v.findViewById(R.id.deviceListLocation);

        }
    }

    // Define Adapter for Device List
    public DeviceAdapter(ArrayList<Device> deviceNames) {
        devices = deviceNames;
    }

    //On Click Listener
    /*
    private OnDeviceCLickListener deviceListener;
    public interface OnDeviceClickListener {
        void onItemClick(int pos);
    }

    public void setOnDeviceClick(OnDeviceClickListener dListener) {
        deviceListener = dListener;
    }
     */

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
        //getResources().getString(
        holder.deviceStatus.setText(d.isStatus() ? R.string.activeDeviceStatus : R.string.inactiveDeviceStatus);
        holder.deviceLocation.setText(d.getDeviceLocation());
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        return devices.size();
    }
}
