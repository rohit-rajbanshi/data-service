package com.gslab.solar.service;

import com.gslab.solar.domain.Panel;
import com.gslab.solar.domain.SensorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringDataParser {

	private String str;
	private Integer totalRec;
	private String panelTag;
	private Map<String, Map> panelData;

	public StringDataParser(String str) {
		this.str = str.trim();
		panelData = new HashMap<String, Map>();
	}

	public void parseData(Map<String,Panel> panels) {

		String temp = str;
		totalRec = Integer.parseInt(temp.substring(0, 2));
		temp = temp.substring(2);
		int i = totalRec;
		while (--i >= 0) {

			Map<String, Double> values = new HashMap<String, Double>();
			String panelId =  temp.substring(0, 3);
			panelTag = panels.get(panelId).getAssetId();
			temp = temp.substring(3);

			Set<SensorType> sensors = panels.get(panelId).getAssetTag().keySet();
			if(sensors.contains(SensorType.VOLTAGE_IN)){
				values.put(
								panels.get(panelId).getAssetTag().get(SensorType.VOLTAGE_IN).getSourceTagId(),
								Double.parseDouble(temp.substring(0, 5))
				);
				temp = temp.substring(5);
			}
			if(sensors.contains(SensorType.CURRENT)){
				values.put(
								panels.get(panelId).getAssetTag().get(SensorType.CURRENT).getSourceTagId(),
								Double.parseDouble(temp.substring(0,5))
				);
				temp = temp.substring(5);
			}
			if(sensors.contains(SensorType.VOLTAGE_OUT)){
				values.put(
								panels.get(panelId).getAssetTag().get(SensorType.VOLTAGE_OUT).getSourceTagId(),
								Double.parseDouble(temp.substring(0,5))
				);
				temp = temp.substring(5);
			}
			if(sensors.contains(SensorType.TEMPERATURE)){
				values.put(
								panels.get(panelId).getAssetTag().get(SensorType.TEMPERATURE).getSourceTagId(),
								Double.parseDouble(temp.substring(0,5))
				);
				temp = temp.substring(5);
			}
			panelData.put(panelTag, values);
		}
	}

	public Integer getTotalRec() {
		return totalRec;
	}

	public void setTotalRec(Integer totalRec) {
		this.totalRec = totalRec;
	}

	public String getPanelTag() {
		return panelTag;
	}

	public void setPanelTag(String panelTag) {
		this.panelTag = panelTag;
	}

	public Map<String, Map> getPanelData() {
		return panelData;
	}

	public void setPanelData(Map<String, Map> panelData) {
		this.panelData = panelData;
	}

}
