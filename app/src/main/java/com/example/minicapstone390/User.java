package com.example.minicapstone390;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User {
    private String userName, userEmail;
    private Map<String, Object> deviceKeys;
    public User() { }

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public Map<String, Object> getDeviceKeys() { return deviceKeys; }

    public void setDeviceKeys(Map<String, Object> deviceKeys) { this.deviceKeys = deviceKeys; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
