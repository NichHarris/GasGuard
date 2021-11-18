
#ifndef _Sensor_h
#ifdef __cplusplus
#define _Sensor_h
#include "Value.h"
using namespace std;

class Sensor : public Sensor {
  public:
    Sensor();
    Sensor(String, Value, int, float);

    String getSensorName();
    float getSensorValue();
    bool getStatus();
    map<String, Value> getSensorPastValues();

    void setSensorName(String);
    void setSensorValue(float);
    void setStatus(bool);
    void addPastValue(Value);

  private:
    float SensorValue;
    String SensorName;
    map<String, Value> SensorPastValues;
    bool status;
}



#endif // __cplusplus
#endif /* _Sensor_h */
