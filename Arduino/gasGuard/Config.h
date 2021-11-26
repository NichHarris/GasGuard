// UPDATE FIREBASE URL AND PASSWORD BEFORE RUNNING

#define DATABASE_URL "gasguard-ae330-default-rtdb.firebaseio.com"
#define DATABASE_SECRET "sef3dUyAPbvVrI6ANAIz3ikrSyg0mLXS7FPbXhHe"

// UPDATE WIFI SSID AND PASSWORD BEFORE RUNNING 

<<<<<<< HEAD
#define WIFI_SSID "Khaled's hotspot"
#define WIFI_PASSWORD "hellohello"

// UPDATE YOUR DEVICE INFORMATION BEFORE RUNNING

#define DeviceID "8001"
#define DeviceName "GasGuard-3000"
#define NumOfSensors 6
// Type of Sensor is name of sensor without MQ
#define NameOfSensors {"MQ2","MQ4","MQ9","MQ7","MQ8","MQ135","",""}
#define TypeOfSensors {2,4,9,7,8,135,0,0}


//in ms
#define Delay 30000

// Total time for calibration is CalDelay*CalNum
// Number of samples used for calibration
#define CalNum 100
// delay between each calibration sample (in ms)
#define CalDelay 1000 
=======
#define WIFI_SSID "TEMP"
#define WIFI_PASSWORD "TEMP"

// UPDATE YOUR DEVICE INFORMATION BEFORE RUNNING

#define DeviceID "203"
#define DeviceName "GasGuard-203"
#define NumOfSensors 8
#define NameOfSensors {"MQ2","MQ3", "MQ4","MQ6", "MQ135", "MQ9","MQ8", "MQ7"}

#define Delay 60000
>>>>>>> 2bc602c55a27b03d1f86b6894055b847793ef470
