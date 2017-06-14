package com.gslab.solar.domain;

public class AssetTag {

  private String complexType;
  private String tagUri;
  private String sourceTagId;
  private double outputMinimum;
  private double outputMaximum;
  private double hiAlarmThreshold;
  private double loAlarmThreshold;

  public String getComplexType() {
    return complexType;
  }

  public void setComplexType(String complexType) {
    this.complexType = complexType;
  }

  public String getTagUri() {
    return tagUri;
  }

  public void setTagUri(String tagUri) {
    this.tagUri = tagUri;
  }

  public String getSourceTagId() {
    return sourceTagId;
  }

  public void setSourceTagId(String sourceTagId) {
    this.sourceTagId = sourceTagId;
  }

  public double getOutputMinimum() {
    return outputMinimum;
  }

  public void setOutputMinimum(double outputMinimum) {
    this.outputMinimum = outputMinimum;
  }

  public double getOutputMaximum() {
    return outputMaximum;
  }

  public void setOutputMaximum(double outputMaximum) {
    this.outputMaximum = outputMaximum;
  }

  public double getHiAlarmThreshold() {
    return hiAlarmThreshold;
  }

  public void setHiAlarmThreshold(double hiAlarmThreshold) {
    this.hiAlarmThreshold = hiAlarmThreshold;
  }

  public double getLoAlarmThreshold() {
    return loAlarmThreshold;
  }

  public void setLoAlarmThreshold(double loAlarmThreshold) {
    this.loAlarmThreshold = loAlarmThreshold;
  }
}
