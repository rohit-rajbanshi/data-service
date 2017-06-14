package com.gslab.solar.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum SensorType {
  TEMPERATURE("temperature"),
  VOLTAGE_IN("voltage-in"),
  VOLTAGE_OUT("voltage-out"),
  CURRENT("current"),
  POWER("power");


  private static Map<String, SensorType> sensorTypes = new HashMap<>(5);

  static {
    sensorTypes.put(TEMPERATURE.type, TEMPERATURE);
    sensorTypes.put(VOLTAGE_IN.type, VOLTAGE_IN);
    sensorTypes.put(VOLTAGE_OUT.type, VOLTAGE_OUT);
    sensorTypes.put(CURRENT.type, CURRENT);
    sensorTypes.put(POWER.type, POWER);
  }

  protected String type;

  private SensorType(String type) {
    this.type = type;
  }

  //@Override
  //public String toString() {
  //	return this.type;
  //}

  @JsonValue
  public String value() {
    return this.type;
  }

  public static SensorType get(String value) {
    return sensorTypes.get(value);
  }

  public static Map<String, SensorType> getSensorTypes() {
    return sensorTypes;
  }

}
