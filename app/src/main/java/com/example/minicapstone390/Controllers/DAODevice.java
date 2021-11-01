package com.example.minicapstone390.Controllers;

import com.example.minicapstone390.Models.Device;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAODevice {

    protected FirebaseDatabase dB;
    private DatabaseReference ref;
    public DAODevice() {
        dB = FirebaseDatabase.getInstance();
        ref = dB.getReference(Device.class.getSimpleName());
    }

    public Task<Void> add(Device device) {
        return ref.push().setValue(device);
    }
}
