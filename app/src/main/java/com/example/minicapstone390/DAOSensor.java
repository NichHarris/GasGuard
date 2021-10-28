package com.example.minicapstone390;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOSensor {

    protected FirebaseDatabase dB;
    private DatabaseReference ref;

    public DAOSensor() {
        dB = FirebaseDatabase.getInstance();
        ref = dB.getReference("Sensor");
    }

    public String add(Sensor sensor) {
        ref.push().setValue(sensor);

        return ref.getKey();
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return ref.child(key).updateChildren(hashMap);
    }
}
