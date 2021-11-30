// UPDATE FIREBASE URL AND PASSWORD BEFORE RUNNING

#define DATABASE_URL "gasguard-ae330-default-rtdb.firebaseio.com"
#define DATABASE_SECRET "sef3dUyAPbvVrI6ANAIz3ikrSyg0mLXS7FPbXhHe"

// UPDATE WIFI SSID AND PASSWORD BEFORE RUNNING 

#define WIFI_SSID "TEMP"
#define WIFI_PASSWORD "TEMP"

// UPDATE YOUR DEVICE INFORMATION BEFORE RUNNING

#define DeviceID "203"
#define DeviceName "GasGuard-203"
#define NumOfSensors 8
#define NameOfSensors {"MQ2","MQ3", "MQ4","MQ6", "MQ135", "MQ9","MQ8", "MQ7"}
#define TypeOfSensors {2,4,9,7,8,135,0,0}

#define Delay 60000


// Total time for calibration is CalDelay*CalNum
// Number of samples used for calibration
#define CalNum 10000
// delay between each calibration sample (in ms)
#define CalDelay 1000 
