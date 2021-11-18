package com.example.minicapstone390.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final FirebaseUser user;

    public Database() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.user = auth.getCurrentUser();
    }

    public FirebaseUser getUser() {
        return this.user;
    }

    public FirebaseAuth getAuth() {
        return this.auth;
    }

    public FirebaseDatabase getDatabase() {
        return this.database;
    }

    public DatabaseReference getUserRef() {
        return this.database.getReference("Users");
    }

    public DatabaseReference getUserChild() { return this.database.getReference("Users").child(getUserId()); }

    public DatabaseReference getUserChild(String node) {
        return getUserRef().child(node);
    }

    public DatabaseReference getDeviceRef() {
        return this.database.getReference("Devices");
    }

    public DatabaseReference getDeviceChild(String node) {
        return getDeviceRef().child(node);
    }

    public DatabaseReference getSensorRef() {
        return this.database.getReference("Sensors");
    }

    public DatabaseReference getSensorChild(String node) {
        return getSensorRef().child(node);
    }

    public String getUserId() {
        return this.user.getUid();
    }
}
