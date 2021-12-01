// UPDATE FIREBASE URL AND PASSWORD BEFORE RUNNING

#define DATABASE_URL "gasguard-ae330-default-rtdb.firebaseio.com"
#define DATABASE_SECRET "sef3dUyAPbvVrI6ANAIz3ikrSyg0mLXS7FPbXhHe"

// UPDATE WIFI SSID AND PASSWORD BEFORE RUNNING 

#define WIFI_SSID "Isengard-Network"
#define WIFI_PASSWORD "Play4Keep$"

// UPDATE YOUR DEVICE INFORMATION BEFORE RUNNING

#define DeviceID "3020"
#define DeviceName "GasGuard-3020"
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
#define CalDelay 5000 
