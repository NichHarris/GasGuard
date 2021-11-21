#include <WiFiNINA.h>
#include <SPI.h>
#include <WiFiUdp.h>
#include <RTCZero.h>

#include "Firebase_Arduino_WiFiNINA.h"
#include "TimeLib.h"
#include "Config.h"

#define TIME_HEADER  "T"   // Header tag for serial time sync message
#define TIME_REQUEST  7    // ASCII bell character requests a time sync message 

FirebaseData fbdo;
int GMT = -5;
RTCZero rtc;

float SensorValue;
String SensorNames[] = NameOfSensors;

void setup() {
  Serial.begin(115200);

  delay(100);
  Serial.println();

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

  //Provide the authentication data
  Firebase.begin(DATABASE_URL, DATABASE_SECRET, WIFI_SSID, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);

  for (int i = 0; i < NumOfSensors; i++) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/sensors/" + String(i), String(DeviceID) + "-" + String(i));
  }

  if (!Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/status")) {
    Firebase.setBool(fbdo, "Devices/" + String(DeviceID) + "/status", true);
  }

  if (!Firebase.getString(fbdo, "Devices/" + String(DeviceID) + "/deviceName")) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/deviceName", DeviceName);
  }

  if (!Firebase.getString(fbdo, "Devices/" + String(DeviceID) + "/location")) {
    Firebase.setString(fbdo, "Devices/" + String(DeviceID) + "/location", "");
  }
  
  for (int i = 0; i < NumOfSensors; i++) {
    Firebase.setString(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorName", SensorNames[i]);
  }
  setTime(WiFi.getTime()); 
  adjustTime(GMT*60*60);
  setSyncProvider(requestSync);  //set function to call when sync required
  Serial.println("Waiting for sync message");
}
void loop() {

  if (Serial.available()) {
    processSyncMessage();
  }

  Firebase.getBool(fbdo, "Devices/" + String(DeviceID) + "/status");
  if (fbdo.boolData() == true) {
    for (int i = 0; i < NumOfSensors; i++) {
      SensorValue = analogRead(i) / 1023.0 * 4.5;
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorType", i);
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID) + "-" + String(i) + "/SensorValue", SensorValue);
      Firebase.setFloat(fbdo, "Sensors/" + String(DeviceID)+"-" + String(i) + "/SensorPastValues/" + Timestamp() + "/Value", SensorValue);
    }
  }
  delay(Delay);
}

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

time_t requestSync()
{
  Serial.write(TIME_REQUEST);
  return 0; // the time will be sent later in response to serial mesg
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
