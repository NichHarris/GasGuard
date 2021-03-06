package com.example.minicapstone390.Controllers;

import com.example.minicapstone390.Models.DatabaseEnv;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Contains references and controls for all database objects
public class Database {
    private static final String DEVICES = DatabaseEnv.DEVICES.getEnv();
    private static final String SENSORS = DatabaseEnv.SENSORS.getEnv();
    private static final String USERS = DatabaseEnv.USERS.getEnv();

    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final FirebaseUser user;

    public Database() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.user = auth.getCurrentUser();
    }

    public FirebaseUser getUser() { return this.user; }

    public FirebaseAuth getAuth() { return this.auth; }

    public FirebaseDatabase getDatabase() { return this.database; }

    public DatabaseReference getUserRef() { return this.database.getReference(USERS); }

    public DatabaseReference getUserChild() { return getUserRef().child(getUserId()); }

    public DatabaseReference getUserChild(String node) { return getUserRef().child(node); }

    public DatabaseReference getDeviceRef() { return this.database.getReference(DEVICES); }

    public DatabaseReference getDeviceChild(String node) { return getDeviceRef().child(node); }

    public DatabaseReference getSensorRef() { return this.database.getReference(SENSORS); }

    public DatabaseReference getSensorChild(String node) { return getSensorRef().child(node); }

    public String getUserId() { return this.user.getUid(); }
}
