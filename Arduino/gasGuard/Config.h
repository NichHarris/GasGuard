// UPDATE FIREBASE URL AND PASSWORD BEFORE RUNNING
#define DATABASE_URL "Enter_DB_Url"
#define DATABASE_SECRET "Enter_DB_Secret"

// UPDATE WIFI SSID AND PASSWORD BEFORE RUNNING 
#define WIFI_SSID "Enter_Wifi_Here"
#define WIFI_PASSWORD "Enter_Password_Here"

// UPDATE YOUR DEVICE INFORMATION BEFORE RUNNING
#define DeviceID "3020"
#define DeviceName "GasGuard-3020"
#define NumOfSensors 6

// Type of Sensor is name of sensor without MQ
#define NameOfSensors {"MQ2","MQ4","MQ9","MQ7","MQ8","MQ135","",""}
#define TypeOfSensors {2,4,9,7,8,135,0,0}

// Define Data Readings Delay (in ms)
#define Delay 30000

// Total time for calibration is CalDelay*CalNum
// Number of samples used for calibration
#define CalNum 1440

// Define Calibration Delay (in ms)
#define CalDelay 15000 
