#include <WiFiNINA.h>
#include <SPI.h>
#include "FlashAsEEPROM.h"
#include "Firebase_Arduino_WiFiNINA.h"

// UPDATE FIREBASE URL AND PASSWORD BEFORE RUNNING
#define DATABASE_URL "gasguard-ae330-default-rtdb.firebaseio.com"
#define DATABASE_SECRET "sef3dUyAPbvVrI6ANAIz3ikrSyg0mLXS7FPbXhHe"

// UPDATE WIFI SSID AND PASSWORD BEFORE RUNNING 
// #define WIFI_SSID "VIRGIN188"
// #define WIFI_PASSWORD "KhaledWIFI"
#define WIFI_SSID "Khaled's hotspot"
#define WIFI_PASSWORD "hellohello"

FirebaseData fbdo;
int dPinMQ2 = 15;
int dPinMQ4 = 14;
int dPinMQ6 = 13;
int dPinMQ7 = 12;
int dPinMQ8 = 11;
int dPinMQ135 = 10;

float MQ2value;
float MQ4value;
float MQ6value;
float MQ7value;
float MQ8value;
float MQ135value; 

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

//  String sensorType = "0"; String sensorData = "123"; String timeStamp = "\"Today\""; 
//  String JSONsensorData = "{\"sensorType\": " + sensorType + ", \"sensorData\": " + sensorData + ", \"timeStamp\": " + timeStamp + "}";
//  Serial.println(JSONsensorData);
//  //String JSONsensorData = "{\"sensorType\":\"value1\", \"sensorData\":{\"_data2\":\"_value2\"}}";
//  if(Firebase.setJSON(fbdo, "/Sensors", JSONsensorData)){
//    Serial.println(fbdo.pushName());
//  } else {
//    // On Fail
//   Serial.println(fbdo.errorReason());
//  }
//
  pinMode(dPinMQ2, INPUT);
  pinMode(dPinMQ4, INPUT);
  pinMode(dPinMQ6, INPUT);
  pinMode(dPinMQ7, INPUT);
  pinMode(dPinMQ8, INPUT);
  pinMode(dPinMQ135, INPUT);

  Firebase.setString(fbdo, "/Sensors/Sensor1/sensorName", "MQ2");
  Firebase.setString(fbdo, "/Sensors/Sensor2/sensorName", "MQ4");
  Firebase.setString(fbdo, "/Sensors/Sensor3/sensorName", "MQ6");
  Firebase.setString(fbdo, "/Sensors/Sensor4/sensorName", "MQ7");
  Firebase.setString(fbdo, "/Sensors/Sensor5/sensorName", "MQ8");
  Firebase.setString(fbdo, "/Sensors/Sensor6/sensorName", "MQ135");
}

void loop() {
   //put your main code here, to run repeatedly:
   if (digitalRead(dPinMQ2)==1)
      MQ2value = analogRead(A0)/1023.0;
   else
      MQ2value = -1;

   if (digitalRead(dPinMQ4)==1)
      MQ4value = analogRead(A1)/1023.0;
   else
      MQ4value = -1;

   if (digitalRead(dPinMQ6)==1)
      MQ6value = analogRead(A2)/1023.0;
   else
      MQ6value = -1;

   if (digitalRead(dPinMQ7)==1)
      MQ7value = analogRead(A3)/1023;
   else
      MQ7value = -1;

   if (digitalRead(dPinMQ8)==1)
      MQ8value = analogRead(A4)/1023.0;
   else
      MQ8value = -1;

   if (digitalRead(dPinMQ135)==1)
      MQ135value = analogRead(A5)/1023.0;
   else
      MQ135value = -1;
   
   Serial.print("MQ2: ");Serial.println(MQ2value);
   Serial.print("MQ4: ");Serial.println(MQ4value);
   Serial.print("MQ6: ");Serial.println(MQ6value);
   Serial.print("MQ7: ");Serial.println(MQ7value);
   Serial.print("MQ8: ");Serial.println(MQ8value);
   Serial.print("MQ135: ");Serial.println(MQ135value);
   Serial.println("");
   
   if(Firebase.setFloat(fbdo, "/Sensors/Sensor1/sensorValue", MQ2value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
  
  if(Firebase.setFloat(fbdo, "/Sensors/Sensor2/sensorValue", MQ4value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
     
  if(Firebase.setFloat(fbdo, "/Sensors/Sensor3/sensorValue", MQ6value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
  
  if(Firebase.setFloat(fbdo, "/Sensors/Sensor4/sensorValue", MQ7value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }
  
  if(Firebase.setFloat(fbdo, "/Sensors/Sensor5/sensorValue", MQ8value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }

  if(Firebase.setFloat(fbdo, "/Sensors/Sensor6/sensorValue", MQ135value)) {
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }

  if( Firebase.pushFloat(fbdo, "/Sensors/Sensor1/sensorData", MQ2value)
   ) {
     Firebase.pushTimestamp(fbdo, fbdo.dataPath()+ + "/"+ fbdo.pushName() + "/Timestamp");
    Serial.println(fbdo.floatData());
  } else {
    Serial.println(fbdo.errorReason());
  }

  
   delay(5000);
}
