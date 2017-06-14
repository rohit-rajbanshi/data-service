package com.gslab.solar.domain;

import java.util.Map;

/**
 * Panel Data Model
 */
public class Panel{

  private String complexType;
  private String uri;
  private String id;
  private String name;
  private String assetId;
  private String description;
  private String group;
  private String parent;
  private String parentUri;
  private Map<SensorType, AssetTag> assetTag;

  public String getComplexType() {
    return complexType;
  }

  public void setComplexType(String complexType) {
    this.complexType = complexType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getParentUri() {
    return parentUri;
  }

  public void setParentUri(String parentUri) {
    this.parentUri = parentUri;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String assetId) {
    this.assetId = assetId;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public Map<SensorType, AssetTag> getAssetTag() {
    return assetTag;
  }

  public void setAssetTag(Map<SensorType, AssetTag> assetTag) {
    this.assetTag = assetTag;
  }

  @Override
  public String toString() {
    return "Panel{" +
            "complexType='" + complexType + '\'' +
            ", uri='" + uri + '\'' +
            ", id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", assetId='" + assetId + '\'' +
            ", description='" + description + '\'' +
            ", group='" + group + '\'' +
            ", parent='" + parent + '\'' +
            ", parentUri='" + parentUri + '\'' +
            ", assetTag=" + assetTag +
            '}';
  }

}
