#include <WiFiNINA.h>
#include <SPI.h>
#include "Firebase_Arduino_WiFiNINA.h"
#include "TimeLib.h" /* Source: https://github.com/PaulStoffregen/Time */
#include "Config.h"

// Define 
#define TIME_HEADER  "T"   // Header tag for serial time sync message
#define TIME_REQUEST  7    // ASCII bell character requests a time sync message 

bool calibrationStatus = false;
FirebaseData fbdo;

int GMT = -5;
float SensorValue;
String SensorNames[] = NameOfSensors;
int SensorTypes[] = TypeOfSensors; 
float CalibratedValues[8] = { 0, 0, 0, 0, 0, 0, 0, 0};

void setup() {
  Serial.begin(115200);

  delay(100);
  Serial.println();
  
  setFirebase();
  
  // Get Time from Wifi Package
  // Adjust & Synchronize the Time 
  setTime(WiFi.getTime()); 
  adjustTime(GMT*60*60);
  setSyncProvider(requestSync);
}

void loop() {
  if (Serial.available()) {
    time_t time = validateTime();
    if (time != 0) {
      setTime(time);
    } else {
      Serial.println("Time invalid.");
    }
  }
  if(!isCalibrated()){
    Calibrate();
  }
  else{
    sendData();
  }
  delay(Delay);
}

// Validate Time and Return Current Time for Sensor Data Time Stamp
unsigned long validateTime() {
  unsigned long time = 0L;
  if (Serial.find(TIME_HEADER)) {
    
    // Validate Time is Valid
    if (Serial.parseInt() >= WiFi.getTime()) {
      time = Serial.parseInt();
    }
  }
  
  return time;
}

time_t requestSync(){
  Serial.write(TIME_REQUEST);
  return 0;
}

void sendData() {
  // Get Data for Device Status from Firebase
  Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/status");


  if (fbdo.boolData() == true) {
    // Update All Sensors Values in Firebase
    for (int i = 0; i < NumOfSensors; i++) {
      // Convert from Voltage to PPM
      SensorValue = analogRead(i) - CalibratedValues[i];
      
      // Negative PPM is Replaced with Zero
      if(SensorValue < 0) {
        SensorValue = 0;
      }

      // Set Sensor Values in Realtime Database
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorValue", SensorValue/0.75);
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID)+"-" + String(i) + "/SensorPastValues/" + Timestamp() + "/Value", SensorValue/0.75);
    }
  }
}

void Calibrate(){
  Serial.println("Calibrating...");
  for(int i = 0; i<CalNum; i++){
        for (int j = 0; j < NumOfSensors; j++) {
          // Sum of all calibration readings
          CalibratedValues[j] = CalibratedValues[j] + analogRead(j); 
        }
        delay(CalDelay);
  }
  
  Serial.println("Calibration Complete");
  calibrationStatus = true;
  delay(1000);
  
  Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus", calibrationStatus);
  Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-0/CalibratedValue", CalibratedValues[0]/CalNum);
  for (int i = 0; i < NumOfSensors; i++) {
    // Get average of all calibrated values
    CalibratedValues[i] = CalibratedValues[i]/CalNum;
    Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/CalibratedValue", CalibratedValues[i]);
  }
  delay(500);
}

bool isCalibrated(){
  Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus");
  return fbdo.boolData();
}
 
void setFirebase(){
  // Connect to Wifi
  Serial.print("Connecting to Wi-Fi");
  int status = WL_IDLE_STATUS;
  while (status != WL_CONNECTED) {
    status = WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print(".");
    delay(100);
  }
  
  // Print to Signal Successful Connection to Wifi 
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP()); 

  // Connect to Firebase Realtime Database
  Firebase.begin(DATABASE_URL, DATABASE_SECRET, WIFI_SSID, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);

  // Set Sensors List of Given Device
  for (int i = 0; i < NumOfSensors; i++) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/sensors/" + String(i), String(DeviceID) + "-" + String(i));
  }

  // Set Device Status
  if (!Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/status:")) {
    Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/status", 1);
  }

  // Set Calibration Status
  if (!Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus:")) {
    Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus", calibrationStatus);
  }

  // Set Device Name
  if (!Firebase.getString(fbdo, "Devices/" + String(DeviceID) + "/deviceName:")) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/deviceName", DeviceName);
  }

  // Set Location 
  if (!Firebase.getString(fbdo, "Devices/" + String(DeviceID) + "/location:")) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/location", "");
  }

  // Set All Sensors' Names, Types, and Status of Given Device
  for (int i = 0; i < NumOfSensors; i++) {
    Firebase.setString(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorName", SensorNames[i]);
    Firebase.setInt(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorType", SensorTypes[i]);
    Firebase.setBool(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/status", true);
  }

  // Set Calibrated Values for All Sensors of Given Device
  if (isCalibrated()) {
    for (int i = 0; i < NumOfSensors; i++) {
      Firebase.getFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/CalibratedValue");
      CalibratedValues[i] = fbdo.floatData();
    }
  }
  delay(500);
}

// Get Time for Each Sensor Reading
String Timestamp() {
  time_t t = now();
  time_t MM = month(t);
  time_t DD = day(t);
  time_t H = hour(t);
  time_t M = minute(t);
  time_t S = second(t);
  String month = String( (unsigned long) MM );
  String day = String( (unsigned long) DD );
  String hour = String( (unsigned long) H );
  String minute = String( (unsigned long) M );
  String second = String( (unsigned long) S );

  if (month.toInt() < 10) {
    month = "0" + month;
  }
  if (day.toInt() < 10) {
    day = "0" + day;
  }
  if (hour.toInt() < 10) {
    hour = "0" + hour;
  }
  if (minute.toInt() < 10) {
    minute = "0" + minute;
  }
  if (second.toInt() < 10) {
    second = "0" + second;
  }
  return String(year(t)) + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
}
