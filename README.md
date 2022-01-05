# GasGuard

Android Application that uses a network of devices to analyze the air quality and the ambient concentration of harmful gases in various locations

Users concerned with lung conditions, workplaces involving hazardous or dusty conditions, or otherwise curious individuals will be able to see the tracking and graphing of these levels through the application on a visually appealing and easily navigable display.

## Application Wireframe
![wireframes](https://user-images.githubusercontent.com/68450354/148159616-e68d4b48-f621-4fa9-bcea-a3bdb433d1dd.png)

## System Architecture
![systemArchitecture](https://user-images.githubusercontent.com/68450354/148159892-d37bb26d-8508-42f0-b74e-e5546178f722.png)

## Hardware Architecture
![hardwareArchitecture](https://user-images.githubusercontent.com/68450354/148159640-16a604d5-b7a2-4ece-bb68-244560559fd8.png)

## Software Architecture
![sofwareClass](https://user-images.githubusercontent.com/68450354/148159636-bb7dd9be-1015-429b-a21a-81a68ccd41e9.png)

## Arduino Nano 33 IOT

- Powered via the breadboard power rail using a barrel jack converter to a USB 3 connected to an A/C wall wort power adapter

- Receives analog inputs via gas sensors and sends sesnor data to Firebase Database over Wi-Fi Connection.

- Arduino and Firebase will communicate via library commands that will create devices and sensors, as well as issue commands to halt and/or turn off the process of collecting and sending data.

### Arduino Libraries

- Firebase_Arduino_WiFiNINA - Connect to the obtained WiFi and the database in firebase used for this project and then send/receive the float values obtained by the sensors directly to the database and tag a timestamp along with it.

- TimeLib library - Track the date and time, formatted as YYYY/MM/DD”T”hh:mm:ss and sent to the database with the sensor data.

## Firebase

### Firebase Authentication

Provides an authentication service, handling both the creation of users and storing their passwords securely. \
Send emails to notify the user of a successful creation of an account and allow the user to reset his or her password using a one-time reset password link.

### Firebase Real Time Database

Provides real time access to and storage of data. \
Using a real time database is necessary due to the severity of the health consequences if a leak or an unsafe environment is detected but the user is not warned in time.

#### Database Schema

“Users”: {

    “$uid”:  {

        userName: {".validate": "newData.isString()"},

        userEmail: {".validate": "newData.isString()"},

        userFirstName: {".validate": "newData.isString()"},

        userLastName: {".validate": "newData.isString()"},

        userPhone: {".validate": "newData.isString()"},

        // Dictionary of Device Ids

        devices: {

            “$i” : {".validate": "newData.isString()"}

        }

    }

}

“Devices”: {

    “$did”:  {

        deviceName: {".validate": "newData.isString()"},

        status: {".validate": "newData.isBoolean()"},

        CalibrationStatus: {".validate": "newData.isBoolean()"},

        location: {“.validate”: “newData.isString()”},

        // Dictionary of Sensor Ids

        sensors: {

            “$i” : {".validate": "newData.isString()"}

        }
    }

}

“Sensors”: {

    “$sid”:  {

        CalibratedValue: {“.validate”: “newData.isDouble()”},

        SensorScore: {“.validate”: “newData.isDouble()”},

        SensorValue: {“.validate”: “newData.isDouble()”},

        SensorName: {".validate": "newData.isString()"},

        SensorType: {".validate": "newData.isLong()"},

        status: {".validate": "newData.isBoolean()"},

        // Dictionary of Past Sensor Data

        SensorPastValues: {

            $i” : {

                “data”: {".validate": "newData.isString()"},

                “timeStamp”: {".validate": "newData.isString()"}

            }

        }

    }

}

## Gas Sensors

**MQ-2**: Combustible gas, Smoke \
**MQ-3**: Alcohol \
**MQ-4**: Methane, Propane, Butane \
**MQ-6**: Liquefied petroleum, butane, propane, LPG \
**MQ-7**: Carbon Monoxide \
**MQ-8**: Hydrogen \
**MQ-9**: Carbon Monoxide, Methane \
**MQ-135**: Ammonia sulfide, Benzene Vapor

## App Screens

MainActivity: Is the launcher screen, used as a loading screen to check whether the user is authenticated and direct the user either to LoginActivity to authenticate or HomeActivity if already authenticated.

LoginActivity: Is the login screen where the user enters his or her email and password to authenticated themselves and directs the user to the HomeActivty upon successful login or the user can navigate to the SignupActivity to create an account.

SignupActivity: Is the signup screen where the user enters his or her name, email, and password with a password confirmation to prevent the creation of an account with a typo in the password field. Upon creating an account and passing the validation requirements, the user is directing to the HomeActvity. Otherwise, the user can choose to navigate to the LoginActivity.

HomeActivity: Is the home screen where the user is greeted with a welcome message along with their username and a list of devices added to the user’s account, obtained from the database. Each item in the list view of devices is clickable and directs the user to the respective device screen using DeviceActivity. Additionally, on each device listed, the user has the option to edit, calibrate or remove the device. On this screen the user can see the status of each device: active, inactive, or calibrating. The user also has access to a device health graph which displays the overall health of a device. The user can also choose from 3 options in a drop down, to add a device, to navigate to the profile screen, or to switch the displayed device names to their IDs.

DeviceActivity: Is the device screen where the device name, device status, and list of sensors for that device are presented to the user, obtained from the database. Each item in the list view of sensors is clickable and directs the user to the respective sensor screen using SensorActivity. From this view, the user has the option to edit the device, calibrate the device, deactivate/activate the device, or remove the device from the user. On the sensor views, the user can choose to edit the sensor name or delete all past data from it. The latest sensor reading is displayed in PPM on each sensor as well as the average value of the past 5 minutes, and the sensor status. When a sensor switches from safe to unsafe, a notification will be generated, from this notification the user can click it to be directed straight to the appropriate sensor page.

SensorActivity: Is the sensor screen where the sensor name, sensor type, gases measured by the sensor and a graph of the measured PPM value from the sensor are presented. This graph can be sorted by data from the past 24 hours, week, 2 weeks, and month. The user can zoom and scroll on the graphs to view the data collected at a more accurate X-axis times in real time. This data is accurate to within 30 seconds of when it was collected. From this view the user has the option to delete all the collected data from the sensor.

ProfileActivity: Is the profile screen where the user can add or edit the basic account information, change the current displayed theme from dark mode to light mode, log out, or completely remove the user's account.

## Team Members

[Nicholas Harris](https://github.com/NichHarris)

[Nicholas Kawwas](https://github.com/nickawwas)

[Mathieu Lebrun](https://github.com/CodenameBrown)

[Khaled Matloub](https://github.com/KhaledMat)

[Julian Dubeau](https://github.com/TheGalefree)

## Dependencies

Used Firebase Authentication and Real Time Database to Obtain/Store User, Device and Sensor Data, MPAndroidChart to Display Live Real Time Sensor Data in Graphs and Bar Charts, and Gradle to Build the Java Project
