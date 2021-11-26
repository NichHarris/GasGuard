#include <ArduinoBLE.h>
#include <WiFiNINA.h>
#include <SPI.h>
#include <WiFiUdp.h>

#include "Firebase_Arduino_WiFiNINA.h"
#include "TimeLib.h"
#include "Config.h"

#define TIME_HEADER  "T"   // Header tag for serial time sync message
#define TIME_REQUEST  7    // ASCII bell character requests a time sync message 

// UUid for Service
const char* UUID_serv = "84582cd0-3df0-4e73-9496-29010d7445dd";

// UUids for WiFi status 
const char* UUID_status   = "84582cd1-3df0-4e73-9496-29010d7445dd";
BLEService myService(UUID_serv); 
BLEFloatCharacteristic  WiFi_status(UUID_status,  BLERead|BLENotify);
bool calibrationStatus = false;
FirebaseData fbdo;

int GMT = -5;
float SensorValue;
String SensorNames[] = NameOfSensors;
int SensorTypes[] = TypeOfSensors; 
float CalibratedValues[8]={0,0,0,0,0,0,0,0};

void setup() {
  Serial.begin(115200);

  delay(100);
  Serial.println();
  
  // setBLE();
  setFirebase();
  
  setTime(WiFi.getTime()); 
  adjustTime(GMT*60*60);
  setSyncProvider(requestSync);  //set function to call when sync required
}
void loop() {

  if (Serial.available()) {
    processSyncMessage();
  }
  if(!isCalibrated()){
    Calibrate();
  }
  else{
    sendData();
  }
  delay(Delay);
}

// functions that helps maintaining the time
void processSyncMessage() {
  unsigned long pctime;
  const unsigned long DEFAULT_TIME = WiFi.getTime(); // Jan 1 2013
  if (Serial.find(TIME_HEADER)) {
    pctime = Serial.parseInt();
    if ( pctime >= DEFAULT_TIME) { // check the integer is a valid time (greater than Jan 1 2013)
      setTime(pctime); // Sync Arduino clock to the time received on the serial port
    }
  }
}

time_t requestSync(){
  Serial.write(TIME_REQUEST);
  return 0; // the time will be sent later in response to serial mesg
}


void sendData(){
  Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/status");
  if (fbdo.boolData() == true) {
    for (int i = 0; i < NumOfSensors; i++) {
      // conversion from Voltage to PPM
      SensorValue = analogRead(i) - CalibratedValues[i];
      
      if(SensorValue < 0){
        SensorValue = 0;
      }
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorValue", SensorValue/0.75);
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID)+"-" + String(i) + "/SensorPastValues/" + Timestamp() + "/Value", SensorValue/0.75);
    }
  }
}
void setBLE(){
  
  Serial.print("Connecting to BLE");
  while (!BLE.begin()) {
    Serial.println("starting BLE failed!");
  }
  
  BLE.setLocalName("GasGuard");
  BLE.setDeviceName("GasGuard"); // Arduino is the default value on this module
  
  // Set advertised Service
  BLE.setAdvertisedService(myService);
  
  // Add characteristics to the Service
  myService.addCharacteristic(WiFi_status);
  
  BLE.addService(myService);
  
}

void Calibrate(){
  Serial.println("Calibrating...");
  for(int i = 0; i<CalNum; i++){
        for (int j = 0; j < NumOfSensors; j++) {
            CalibratedValues[j] = CalibratedValues[j] + analogRead(j); // get sum of all readings
        }
        delay(CalDelay);
  }
  
  Serial.println("Calibration Complete");
  calibrationStatus = true;
  delay(1000);
  // Arduino sucks for absolutely no reason this works...
  Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus:", calibrationStatus);
  Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus", calibrationStatus);
  Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-0/CalibratedValue", CalibratedValues[0]/CalNum);
  for (int i = 0; i < NumOfSensors; i++) {
      CalibratedValues[i] = CalibratedValues[i]/CalNum;
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/CalibratedValue", CalibratedValues[i]); //send the average value back
  }
  delay(500);
}

bool isCalibrated(){
  Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus");
  return fbdo.boolData();
}
 
void setFirebase(){
  Serial.print("Connecting to Wi-Fi");
  int status = WL_IDLE_STATUS;
  while (status != WL_CONNECTED)
  {
    status = WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print(".");
    delay(100);
  }
  Serial.println(); 
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP()); 
  Serial.println();

  Firebase.begin(DATABASE_URL, DATABASE_SECRET, WIFI_SSID, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);

  for (int i = 0; i < NumOfSensors; i++) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/sensors/" + String(i), String(DeviceID) + "-" + String(i));
  }
  if (!Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/status:")) {
    Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/status", 1);
  }
//  if (!Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus:")) {
//    Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/CalibrationStatus", calibrationStatus);
//  }
  if (!Firebase.getString(fbdo, "Devices/" + String(DeviceID) + "/deviceName:")) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/deviceName", DeviceName);
  }
  if (!Firebase.getString(fbdo, "Devices/" + String(DeviceID) + "/location:")) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/location", "");
  }
  for (int i = 0; i < NumOfSensors; i++) {
    Firebase.setString(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorName", SensorNames[i]);
    Firebase.setInt(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorType", SensorTypes[i]);
  }
  if (isCalibrated()) {
    for (int i = 0; i < NumOfSensors; i++) {
      Firebase.getFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/CalibratedValue");
      CalibratedValues[i] = fbdo.floatData();
    }
  }
  delay(500);
}

String Timestamp()
{
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
