#include <WiFiNINA.h>
#include <SPI.h>
#include "FlashAsEEPROM.h"
#include "Firebase_Arduino_WiFiNINA.h"
# include "TimeLib.h"
#include "Config.h"

#define TIME_HEADER  "T"   // Header tag for serial time sync message
#define TIME_REQUEST  7    // ASCII bell character requests a time sync message 

FirebaseData fbdo;

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
  
  //Provide the autntication data
  Firebase.begin(DATABASE_URL, DATABASE_SECRET, WIFI_SSID, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);
  Firebase.setString(fbdo, "Arduinos/"+String(DeviceID)+"/Sensor1ID", String(DeviceID)+"-1");
  Firebase.setString(fbdo, "Arduinos/"+String(DeviceID)+"/Sensor2ID", String(DeviceID)+"-2");
  Firebase.setString(fbdo, "Arduinos/"+String(DeviceID)+"/Sensor3ID", String(DeviceID)+"-3");
  Firebase.setString(fbdo, "Arduinos/"+String(DeviceID)+"/Sensor4ID", String(DeviceID)+"-4");
  Firebase.setString(fbdo, "Arduinos/"+String(DeviceID)+"/Sensor5ID", String(DeviceID)+"-5");
  Firebase.setString(fbdo, "Arduinos/"+String(DeviceID)+"/Sensor6ID", String(DeviceID)+"-6");
  
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorName", Sensor1Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-2/SensorName", Sensor2Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-3/SensorName", Sensor3Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-4/SensorName", Sensor4Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-5/SensorName", Sensor5Name);
  Firebase.setString(fbdo, "Sensors/"+String(DeviceID)+"-6/SensorName", Sensor6Name);

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
   
   if(Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorValue", Sensor1Value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
  
  if(Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-2/SensorValue", Sensor2Value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
     
  if(Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-3/SensorValue", Sensor3Value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
  
  if(Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-4/SensorValue", Sensor4Value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
  
  if(Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-5/SensorValue", Sensor5Value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }

  if(Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-6/SensorValue", Sensor6Value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
//
  if( Firebase.setFloat(fbdo, "Sensors/"+String(DeviceID)+"-1/SensorPastValues/"+Timestamp(),Sensor1Value)
   ) {
         Serial.println(fbdo.floatData());
    } else {
    Serial.println(fbdo.errorReason());
   }
  
   delay(5000);
}

void processSyncMessage() {
  unsigned long pctime;
  const unsigned long DEFAULT_TIME = 1357041600; // Jan 1 2013

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
  return String(year(t))+"-"+String(month(t))+"-"+String(day(t))+"_"+String(hour(t))+":"+String(minute(t))+":"+String(second(t));
}
