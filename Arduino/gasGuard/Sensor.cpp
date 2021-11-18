#include "Sensor.h"
#include "Value.h"

Sensor::Sensor() {
  this.SensorValue = 0;
  this.SensorName = "";
  this.SensorType = 0;
  this.SensorPastValues = new map<String, Value>();
  this.status = false;
}

Sensor::Sensor(String SensorName, Value value, int SensorType, float SensorValue, ) {
  this.SensorValue = SensorValue;
  this.SensorName = SensorName;
  this.SensorType = SensorType;
  this.SensorPastValues.insert(value.getTimeStamp(), value.getValue(); // hmm not sure how to go about this... also this should probs just initialize an empty map do this add in the addPastValue
  this.status = true;
}


String Sensor::getSensorName() {
  return this.SensorName;
}
float Sensor::getSensorValue() {
  return this.SensorValue;
}
bool Sensor::getStatus() {
  return this.status;
}
map<String, Value> Sensor::getSensorPastValues() {
  return this.SensorPastValues;
}

void Sensor::setSensorName(String SensorName) {
  this.SensorName = SensorName
}
void Sensor::setSensorValue(float SensorValue) {
  this.SensorValue = SensorValue
}
void Sensor::setStatus(bool status) {
  this.status = status;
}
void Sensor::addPastValue(Value) {
  // do logic here to add....
}
