package com.example.minicapstone390;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minicapstone390.Models.Sensor;
import com.example.minicapstone390.Views.DeviceActivity;

import java.util.ArrayList;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    // Define Context and ArrayList of Sensors
    private Context mContext;
    private final ArrayList<Sensor> sensors;

    // Define Single Device Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView sensorImg, editIcon, deleteIcon;
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

            sensorImg = (ImageView) v.findViewById(R.id.sensorListImage);
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
                int sensorIndex = getAdapterPosition();
                System.out.println("ACCESS " + sensorIndex);
                ((DeviceActivity)mContext).goToSensorActivity(sensorIndex);
            });
        }
    }

    // Define Adapter for Device List
    public SensorAdapter(ArrayList<Sensor> sensorNames) {
        sensors = sensorNames;
    }

    @NonNull
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

        int type = s.getSensorType();
        String sensorTypeText = "Sensor: MC" + type;
        String liveDataText = "Live: 1.0 ppm";
        String pastDataText = "Prev: 2.0 ppm";

        holder.sensorName.setText(s.getSensorName());
        holder.sensorStatus.setText(s.getStatus() ? R.string.safeSensorValue : R.string.unsafeSensorValue);
        holder.sensorType.setText(sensorTypeText);

        //TODO: Sensor Current and Previous Values
        holder.sensorCurrValue.setText(liveDataText);
        holder.sensorPrevValue.setText(pastDataText);

        // Update Image Based on Sensor Type
        holder.sensorImg.setImageResource(getSensorImg(type));
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        return sensors.size();
    }

    // Get Sensor Drawable Image Resource Based on Type
    public int getSensorImg(int type) {
        switch(type) {
            case 2:
                return R.drawable.mc_2;
            case 3:
                return R.drawable.mc_3;
            case 4:
                return R.drawable.mc_4;
            case 5:
                return R.drawable.mc_5;
            case 6:
                return R.drawable.mc_6;
            case 7:
                return R.drawable.mc_7;
            case 8:
                return R.drawable.mc_8;
            case 9:
                return R.drawable.mc_9;
            default:
                return R.drawable.mc_135;
        }
    }
}
