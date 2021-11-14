package com.example.minicapstone390;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.minicapstone390.Models.Sensor;

import java.util.ArrayList;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    // Define ArrayList of Sensors
    private ArrayList<Sensor> sensors;
    private Context mContext;

    // Define Single Device Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView editIcon, deleteIcon;
        public TextView sensorName, sensorStatus, sensorType, sensorCurrValue, sensorPrevValue;

        public ViewHolder(View v) {
            super(v);

            // Get Context to Call Function in HomeActivity
            mContext = v.getContext();

            // Initialize Values
            sensorName = (TextView) v.findViewById(R.id.sensorListName);
            sensorStatus = (TextView) v.findViewById(R.id.sensorListStatus);
            sensorType = (TextView) v.findViewById(R.id.sensorListType);
            sensorCurrValue = (TextView) v.findViewById(R.id.sensorListCurrentValue);
            sensorPrevValue = (TextView) v.findViewById(R.id.sensorListPastValue);

            editIcon = (ImageView) v.findViewById(R.id.sensorEditIcon);
            deleteIcon = (ImageView) v.findViewById(R.id.sensorDeleteIcon);

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
                //((DeviceActivity)mContext).goToSensorActivity(deviceIndex);
            });
        }
    }

    // Define Adapter for Device List
    public SensorAdapter(ArrayList<Sensor> sensorNames) {
        sensors = sensorNames;
    }

    @Override
    public SensorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create View Based on Specified Layout for Holder
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sensor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Set Data to Recycler View List
        Sensor s = sensors.get(position);

        holder.sensorName.setText(s.getSensorName());
        holder.sensorStatus.setText(s.getStatus() ? R.string.safeSensorValue : R.string.unsafeSensorValue);
        holder.sensorType.setText("Sensor: MC" + s.getSensorType());

        //TODO: Sensor Current and Previous Values
        holder.sensorCurrValue.setText("Live: 1.0 ppm");
        holder.sensorPrevValue.setText("Prev: 2.0 ppm");
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        return sensors.size();
    }
}
