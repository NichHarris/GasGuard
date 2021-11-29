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

import com.example.minicapstone390.Models.Sensor;
import com.example.minicapstone390.R;
import com.example.minicapstone390.Views.DeviceActivity;
import com.example.minicapstone390.Views.SensorActivity;
import com.example.minicapstone390.Views.SensorFragment;

import java.util.ArrayList;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private static final String TAG = "SensorAdapter";
    private static final String sensorIdCall = "sensorId";
    private static final String callFunction = "callFunction";
    private static final String DELETE = "DELETE ";
    private static final String EDIT = "EDIT ";
    private static final String ACCESS = "ACCESS ";

    private final String deleteFunction = "deleteSensor()";
    private final String editFunction = "editSensor()";

    // Define Context and ArrayList of Sensors
    private final Database dB = new Database();
    private Context mContext;
    private final ArrayList<Sensor> sensors;
    public double liveData = 0;

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
                callActivity(editFunction, EDIT, getAdapterPosition());
            });

            deleteIcon.setOnClickListener((view) -> {
                callActivity(deleteFunction, DELETE, getAdapterPosition());
            });

            v.setOnClickListener((view) -> {
                int sensorIndex = getAdapterPosition();
                Log.i(TAG,ACCESS + sensorIndex);
                ((DeviceActivity)mContext).goToSensorActivity(sensorIndex);
            });
        }

    }

    private void callActivity(String function, String type, int position) {
        Intent intent = new Intent(mContext, SensorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(sensorIdCall, sensors.get(position).getId());
        bundle.putString(callFunction, function);
        intent.putExtras(bundle);
        Log.i(TAG,type + position);
        mContext.startActivity(intent);
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
        String sensorTypeText = "Sensor: MQ" + type;
        String liveDataText = String.format("Live: %.3f", s.getSensorValue());
        String pastDataText = String.format("Avg: %.3f", s.getSensorScore());
        holder.sensorName.setText(s.getSensorName());
        holder.sensorStatus.setText(s.getStatus() ? R.string.safeSensorValue : R.string.unsafeSensorValue);
        holder.sensorType.setText(sensorTypeText);

        holder.sensorCurrValue.setText(liveDataText);
        holder.sensorPrevValue.setText(pastDataText);

        // Update Image Based on Sensor Type
        holder.sensorImg.setImageResource(getSensorImg(type));
    }

    @Override
    public int getItemCount() {
        // Return Num Devices
        if (sensors != null) {
            return sensors.size();
        }
        return 0;
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
