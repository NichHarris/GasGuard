package com.example.minicapstone390;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref =db.getReference("sensors");

}
