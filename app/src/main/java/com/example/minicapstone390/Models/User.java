package com.example.minicapstone390.Models;

// Class defining structure of a user
public class User {
    private String userName, userEmail, userPhone, userFirstName, userLastName;

    public User() {}

    // Signup user call
    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = "";
        this.userFirstName = "";
        this.userLastName = "";
    }

    // Update user call
    public User(String userName, String userEmail, String userPhone, String userFirstName, String userLastName) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

    public String getUserPhone() { return this.userPhone; }

    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserFirstName() { return this.userFirstName; }

    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public String getUserLastName() { return this.userLastName; }

    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public String getUserName() { return this.userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return this.userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
