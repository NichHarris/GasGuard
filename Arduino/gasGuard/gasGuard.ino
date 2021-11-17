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

float Sensor1Value;
float Sensor2Value;
float Sensor3Value;
float Sensor4Value;
float Sensor5Value;
float Sensor6Value; 

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
  Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/sensors/0", String(DeviceID)+"-1");
  Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/sensors/1", String(DeviceID)+"-2");
  Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/sensors/2", String(DeviceID)+"-3");
  Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/sensors/3", String(DeviceID)+"-4");
  Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/sensors/4", String(DeviceID)+"-5");
  Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/sensors/5", String(DeviceID)+"-6");
  Firebase.setBool(fbdo, "Devices/"+String(DeviceID)+"/status", true);
  if(!Firebase.getString(fbdo, "Devices/"+String(DeviceID)+"/deviceName")){
    Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/deviceName", "");
      Serial.println("device name created");
    }
  if(!Firebase.getString(fbdo, "Devices/"+String(DeviceID)+"/location")){
    Firebase.setString(fbdo, "Devices/"+String(DeviceID)+"/location", "");
    Serial.println("location");
   }
  
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorName", Sensor1Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-2/SensorName", Sensor2Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-3/SensorName", Sensor3Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-4/SensorName", Sensor4Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-5/SensorName", Sensor5Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-6/SensorName", Sensor6Name);
  
  setTime(WiFi.getTime());
  adjustTime(GMT*60*60);
  setSyncProvider(requestSync);  //set function to call when sync required
  Serial.println("Waiting for sync message");
}

void loop() {
  
  if (Serial.available()) {
    processSyncMessage();
  }

      Sensor1Value = analogRead(A0)/1023.0;
      Sensor2Value = analogRead(A1)/1023.0;
      Sensor3Value = analogRead(A2)/1023.0;
      Sensor4Value = analogRead(A3)/1023.0;
      Sensor5Value = analogRead(A4)/1023.0;
      Sensor6Value = analogRead(A5)/1023.0;

  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorType", 2);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorValue", Sensor1Value);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorPastValues/"+Timestamp()+"/Value",Sensor1Value);

  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-2/SensorType", 3);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-2/SensorValue", Sensor2Value);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-2/SensorPastValues/"+Timestamp()+"/Value",Sensor2Value);  

  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-3/SensorType", 4);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-3/SensorValue", Sensor3Value);  
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-3/SensorPastValues/"+Timestamp()+"/Value",Sensor3Value);

  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-4/SensorType", 6);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-4/SensorValue", Sensor4Value);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-4/SensorPastValues/"+Timestamp()+"/Value",Sensor4Value);

  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-5/SensorType", 7);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-5/SensorValue", Sensor5Value);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-5/SensorPastValues/"+Timestamp()+"/Value",Sensor5Value);

  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-6/SensorType", 8);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-6/SensorValue", Sensor6Value);
  Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-6/SensorPastValues/"+Timestamp()+"/Value",Sensor6Value);
  
  delay(500);
}

void processSyncMessage() {
  unsigned long pctime;
  const unsigned long DEFAULT_TIME = WiFi.getTime(); // Jan 1 2013

  if(Serial.find(TIME_HEADER)) {
     pctime = Serial.parseInt();
     if( pctime >= DEFAULT_TIME) { // check the integer is a valid time (greater than Jan 1 2013)
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
  
  if (month.toInt()<10){
    month = "0"+month;
    }
  if (day.toInt()<10){
    day = "0"+day;
    }
  if (hour.toInt()<10){
    hour = "0"+hour;
    }
  if (minute.toInt()<10){
    minute = "0"+minute;
    }
  if (second.toInt()<10){
    second = "0"+second;
    }
  return String(year(t))+"-"+month+"-"+day+"T"+hour+":"+minute+":"+second;
}
