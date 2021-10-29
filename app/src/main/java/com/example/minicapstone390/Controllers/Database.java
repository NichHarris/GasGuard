package com.example.minicapstone390.Controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref =db.getReference("sensors");

    //        DAOSensor dao = new DAOSensor();
//        Sensor sensor = new Sensor(2, 3.5, "1998-12-12 11:11", "temp");
//        String key = dao.add(sensor);
//        System.out.println(key);
//
//        Device device = new Device("3", "Test2", false);
//        DAODevice daoDevice = new DAODevice();
//        daoDevice.add(device);

}
